package com.kii.beehive.portal.manager;


import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.helper.AuthInfoService;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.context.UserTokenBindTool;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.exception.KiiCloudException;

@Component
@Transactional
public class AuthManager {

	private Logger log = LoggerFactory.getLogger(AuthManager.class);

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private AuthInfoService authService;

	@Autowired
	private UserTokenBindTool userTokenBindTool;

	@Autowired
	private KiiUserService userService;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;


	private Map<String,String> oneTimeTokenMap=new ConcurrentHashMap<>();


	public String activite(String userName, String token) {

		BeehiveUser user = userDao.getUserByName(userName);

		if(StringUtils.isEmpty(user.getActivityToken())){
			throw new UnauthorizedException("userID  already activied");
		}

		if(!user.getActivityToken().equals(user.getHashedPwd(token))){
			throw new UnauthorizedException("activity token  incorrect");
		};

		String oneTimeToken= StringRandomTools.getRandomStr(32);

		oneTimeTokenMap.put(user.getUserName(),oneTimeToken);

		return oneTimeToken;

	}

	public void initPassword(String token,String userName,String newPassword) {

		if(StringUtils.isEmpty(token)||!token.equals(oneTimeTokenMap.get(userName))){

			throw new UnauthorizedException("token invalid");

		}

		BeehiveUser  user=userDao.getUserByName(userName);

		String pwd=user.getDefaultPassword();
		String newPwd=user.getHashedPwd(newPassword);

		userService.bindToUser(user,pwd);

		userService.changePassword(pwd,newPwd);

		oneTimeTokenMap.remove(userName);

		userDao.cleanActivityToken(user.getId(),newPwd);
	}



	/**
	 * login Kii Cloud and save the auth info
	 *
	 * @param userName
	 * @param password
	 * @param permanentToken true: save auth info into permanent token cache and DB;
	 *                       false: save auth info into auth info cache only
	 * @return
	 */
	public AuthRestBean login(String userName, String password, boolean permanentToken) {

		BeehiveUser  user=userDao.getUserByName(userName);
		String pwd=user.getHashedPwd(password);

		String token = userService.bindToUser(user, pwd);

		if(token == null) {
			throw new UnauthorizedException( "Authentication failed");
		}

		Team team=this.getTeamByID(user.getId());
		saveToken(permanentToken, user, token,team);

		AuthRestBean authRestBean = generAuthBean(user, token, team);
		return authRestBean;
	}

	private AuthRestBean generAuthBean(BeehiveUser user, String token, Team team) {
		AuthRestBean authRestBean = new AuthRestBean();
		authRestBean.setUser(user);

		if(team != null){
			authRestBean.setTeamID(team.getId());
			authRestBean.setTeamName(team.getName());
		}

		authRestBean.setAccessToken(token);
		return authRestBean;
	}

	private AuthInfo saveToken(boolean permanentToken, BeehiveUser user, String token,Team team) {
		AuthInfo entity = new AuthInfo();
		entity.setUserID(user.getId());

		if(team!=null) {
			entity.setTeamID(team.getId());
		}
		entity.setToken(token);

		Calendar calendar= Calendar.getInstance();
		if(permanentToken) {
			calendar.add(Calendar.MONTH, 1);
		}else{
			calendar.add(Calendar.DAY_OF_YEAR,1);
		}
		entity.setExpireTime(calendar.getTime());

		authService.createAuthInfoEntry(entity,token,permanentToken);
		return entity;
	}

	/**
	 * change the Kii user password in Kii Cloud;
	 * if success, remove the token
	 *
	 * @param oldPassword
	 * @param newPassword
	 */
	public void changePassword(String oldPassword, String newPassword) {

		BeehiveUser  user=userDao.getUserByID(AuthInfoStore.getUserID());

		String pwd=user.getHashedPwd(oldPassword);
		String newPwd=user.getHashedPwd(newPassword);

		userService.changePassword(pwd, newPwd);

		userDao.cleanActivityToken(user.getId(),newPwd);

		authService.removeTokenByUserID(user.getId());

	}


	public String  resetPwd(String userID) {

		BeehiveUser user=userDao.getUserByID(userID);

		userService.bindToUser(user,user.getUserPassword());

		userService.changePassword(user.getUserPassword(),user.getDefaultPassword());

		String token= StringRandomTools.getRandomStr(6);

		user.setActivityToken(user.getHashedPwd(token));

		userDao.updateEntity(user,user.getId());

		authService.removeTokenByUserID(user.getId());

		return token;

	}


	/**
	 * validate user token,
	 * if token is valid, set it into ThreadLocal
	 *
	 * @param token
	 * @return
	 */
	public AuthInfo validateAndBindUserToken(String token)  {

		// try to get auth info from auth info cache by token
		AuthInfo authInfo = authService.getAuthInfoByToken(token);


		// if auth info not found in both cache and DB, throw Exception
		if (authInfo == null) {
			throw new UnauthorizedException("invaild token");
		}

		userTokenBindTool.bindToken(authInfo.getToken());

		return authInfo;
	}

	/**
	 * remove the token
	 *
	 * @param token
	 */
	public void logout(String token) {
		authService.removeToken(token);


	}

	/**
	 * clean the user token in ThreadLocal
	 */
	public void unbindUserToken() {
		userTokenBindTool.clean();
	}


	private  Team getTeamByID(String userID) {
		List<Team> teamList = teamDao.findTeamByUserID(userID);
		if (teamList != null && teamList.size() > 0) {
			return teamList.get(0);
		} else {
			return null;
		}
	}

	public AuthRestBean validateUserToken(String token){

		userTokenBindTool.bindToken(token);

		try {

			KiiUser kiiUser = userService.getKiiUser();

			String userID = kiiUser.getLoginName();

			BeehiveUser beehiveUser = userDao.getUserByID(userID);
			Team team = getTeamByID(userID);

			saveToken(false, beehiveUser, token, team);

			AuthRestBean authRestBean = generAuthBean(beehiveUser, token, team);

			return authRestBean;
		}catch(KiiCloudException ex){
			throw new UnauthorizedException("invaild token");
		}
	}

}

