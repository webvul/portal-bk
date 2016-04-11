package com.kii.beehive.portal.manager;


import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.helper.AuthInfoService;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;

@BindAppByName(appName = "master")
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

	public boolean activity(String userName, String token) {

		BeehiveUser user = userDao.getUserByName(userName);


		if(StringUtils.isEmpty(user.getActivityToken())){
			return false;
		}
		boolean sign = user.getActivityToken().equals(user.getHashedPwd(token));

		if (sign) {
			//one-time token
			userDao.updateWithVerify(user.getId(), Collections.singletonMap("activityToken", null), user.getVersion());


		}

		return sign;
	}

	public void initPassword(String userName,String newPassword) {


		BeehiveUser  user=userDao.getUserByName(userName);

		String pwd=user.getDefaultPassword();

		userService.bindToUser(user,pwd);

		String newPwd=user.getHashedPwd(newPassword);

		userService.changePassword(pwd,newPwd);

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

		AuthInfo entity = new AuthInfo();
		entity.setUserID(user.getId());
		Team team=this.getTeamByID(user.getId());
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

		AuthRestBean authRestBean = new AuthRestBean();
		authRestBean.setUser(user);

		if(team != null){
			authRestBean.setTeamID(team.getId());
			authRestBean.setTeamName(team.getName());
		}

		authRestBean.setAccessToken(token);
		return authRestBean;
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

		userService.bindToUser(user, pwd);

		userService.changePassword(pwd, newPwd);

		authService.removeTokenByUserID(user.getId());

	}



	/**
	 * validate user token,
	 * if token is valid, set it into ThreadLocal
	 *
	 * @param token
	 * @return
	 */
	public AuthInfo validateAndBindUserToken(String token) throws UnauthorizedException {

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

		AuthInfo entry = authService.getAuthInfoByToken(token);

		// this case would rarely happen, because token is validated in AuthInterceptor before coming here
		if(entry == null) {
			throw new UnauthorizedException("the token cannot been found");
		}

		String userID = entry.getUserID();


		AuthRestBean authRestBean = new AuthRestBean();

		// get access token
		String accessToken = entry.getToken();
		authRestBean.setAccessToken(accessToken);

		// get user info
		BeehiveUser beehiveUser = userDao.getUserByID(userID);

		authRestBean.setUser(beehiveUser);

		Team team = getTeamByID(userID);
		if(team != null){
			authRestBean.setTeamID(team.getId());
			authRestBean.setTeamName(team.getName());
		}


		return authRestBean;
	}

}

