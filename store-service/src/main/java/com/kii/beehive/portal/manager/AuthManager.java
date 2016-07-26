package com.kii.beehive.portal.manager;


import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StringRandomTools;
import com.kii.beehive.portal.entitys.AuthInfo;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.exception.UserExistException;
import com.kii.beehive.portal.exception.UserNotExistException;
import com.kii.beehive.portal.helper.AuthInfoService;
import com.kii.beehive.portal.helper.RuleSetService;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.exception.KiiCloudException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Transactional
public class AuthManager {

	private Logger log = LoggerFactory.getLogger(AuthManager.class);

	@Autowired
	private BeehiveUserJdbcDao userDao;


	@Autowired
	private RuleSetService ruleService;


	@Autowired
	private AuthInfoService authService;
//
//	@Autowired
//	private AppBindToolResolver resolver;


	@Autowired
	private KiiUserService userService;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	protected TeamUserRelationDao teamUserRelationDao;


	private Map<String, String> oneTimeTokenMap = new ConcurrentHashMap<>();


	public Map<String, String> createUserDirectly(BeehiveJdbcUser user, String password) {


		BeehiveJdbcUser existsUser = userDao.getUserByLoginId(user);

		if (existsUser != null) {
			throw new UserExistException(user, existsUser);
		}

		user.setEnable(true);
		userDao.addUser(user);

		String pwd = user.getHashedPwd(password);

		String loginID = userService.addBeehiveUser(user, pwd);

		user.setKiiUserID(loginID);

		user.setUserPassword(pwd);

		userDao.updateEntityAllByID(user);

		String token = userService.bindToUser(user, pwd);

		String beehiveToken = getBeehiveToken(token, user.getUserName(), false);

		this.saveToken(user, beehiveToken, null, false);

		Map<String, String> result = new HashMap<>();

		result.put("userID", user.getUserID());
		result.put("token", beehiveToken);

		return result;


	}


	public String activiteByID(String userID, String token) {

		BeehiveJdbcUser user = userDao.getUserByUserID(userID);

		return doActivity(token, user);

	}

	public String activite(String userName, String token) {

		BeehiveJdbcUser user = userDao.getUserByName(userName);
		if (user == null) {
			throw new UserNotExistException(userName);
		}
		return doActivity(token, user);

	}

	private String doActivity(String token, BeehiveJdbcUser user) {
		if (user.getEnable()) {
			throw new UnauthorizedException(UnauthorizedException.USER_ALREADY_ACTIVIED);
		}

		if (!user.getActivityToken().equals(user.getHashedPwd(token))) {
			throw new UnauthorizedException(UnauthorizedException.ACTIVITY_TOKEN_INVALID);
		}

		String oneTimeToken = StringRandomTools.getRandomStr(32);

		oneTimeTokenMap.put(user.getUserName(), oneTimeToken);

		return oneTimeToken;
	}

	public void initPassword(String token, String userName, String newPassword) {

		if (StringUtils.isBlank(token) || !token.equals(oneTimeTokenMap.get(userName))) {

			throw new UnauthorizedException(UnauthorizedException.ACTIVITY_TOKEN_INVALID);

		}

		BeehiveJdbcUser user = userDao.getUserByName(userName);

		String pwd = user.getDefaultPassword();
		String newPwd = user.getHashedPwd(newPassword);

		userService.bindToUser(user, pwd);

		userService.changePassword(pwd, newPwd);

		oneTimeTokenMap.remove(userName);

		userDao.setPassword(user.getId(), newPwd);
	}


	/**
	 * login Kii Cloud and save the auth info
	 *
	 * @param userName
	 * @param password
	 * @return
	 */
	public AuthRestBean login(String userName, String password) {

		BeehiveJdbcUser user = userDao.getUserByName(userName);

		if (user == null) {
			throw new UserNotExistException(userName);
		}


		if (!user.getEnable()) {
			UnauthorizedException excep = new UnauthorizedException(UnauthorizedException.USER_BEEN_LOCKED);
			excep.addParam("userName", userName);
			throw excep;
		}

		String pwd = user.getHashedPwd(password);

		String token = userService.bindToUser(user, pwd);


		String beehiveToken = getBeehiveToken(token, userName, false);

		Team team = this.getTeamByID(user.getId());
		saveToken(user, beehiveToken, team, false);

		AuthRestBean authRestBean = generAuthBean(user, beehiveToken, team);
		return authRestBean;
	}

	public AuthRestBean validateLoginAccessToken(String token) {
		AuthInfo authInfo = authService.getAuthInfoByToken(token);

		// if auth info not found in both cache and DB, throw Exception
		if (authInfo == null) {
			throw new UnauthorizedException(UnauthorizedException.LOGIN_TOKEN_INVALID, "token", token);
		}

		BeehiveJdbcUser user = userDao.getUserByID(authInfo.getUserID());
		Team team = this.getTeamByID(user.getId());
		AuthRestBean authRestBean = generAuthBean(user, token, team);
		return authRestBean;
	}


	private static String getBeehiveToken(String token, String userName, boolean sign) {
		return DigestUtils.sha1Hex(token + "_userName_" + userName + "_3rdPartySign" + sign + "_beehive_token");
	}


	public AuthRestBean getTokenByID(String userID) {

		BeehiveJdbcUser user = userDao.getUserByUserID(userID);

		Team team = this.getTeamByID(user.getId());

		String beehiveToken = getBeehiveToken(StringRandomTools.getRandomStr(16), userID, true);

		saveToken(user, beehiveToken, team, true);

		AuthRestBean authRestBean = generAuthBean(user, beehiveToken, team);
		return authRestBean;
	}

	private AuthRestBean generAuthBean(BeehiveJdbcUser user, String token, Team team) {
		AuthRestBean authRestBean = new AuthRestBean();
		authRestBean.setUser(user);

		if (team != null) {
			authRestBean.setTeamID(team.getId());
			authRestBean.setTeamName(team.getName());
		}

		authRestBean.setAccessToken(token);
		return authRestBean;
	}

	private AuthInfo saveToken(BeehiveJdbcUser user, String token, Team team, boolean is3rdParty) {
		AuthInfo entity = new AuthInfo();
		entity.setUserID(user.getId());
		if (team != null) {
			entity.setTeamID(team.getId());
		}

		Calendar calendar = Calendar.getInstance();
		if (is3rdParty) {
			calendar.add(Calendar.HOUR, 2);
		} else {
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		entity.setExpireTime(calendar.getTime());
		entity.setIs3Party(is3rdParty);

		authService.createAuthInfoEntry(entity, token);
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

		BeehiveJdbcUser user = userDao.getUserByID(AuthInfoStore.getUserID());

		String pwd = user.getHashedPwd(oldPassword);
		String newPwd = user.getHashedPwd(newPassword);

		userService.bindToUser(user, pwd);
		userService.changePassword(pwd, newPwd);

		userDao.setPassword(user.getId(), newPwd);

		authService.removeTokenByUserID(user.getUserID());

	}


	public String resetPwd(String userID) {

		BeehiveJdbcUser user = userDao.getUserByUserID(userID);

		if (user == null) {
			throw new UserNotExistException(userID);
		}

		userService.bindToUser(user, user.getUserPassword());

		userService.changePassword(user.getUserPassword(), user.getDefaultPassword());

		String token = StringRandomTools.getRandomStr(6);

		user.setActivityToken(user.getHashedPwd(token));
		user.setUserPassword(user.getDefaultPassword());
		user.setEnable(false);

		userDao.updateEntityAllByID(user);

		authService.removeTokenByUserID(user.getUserID());

		return token;

	}


	/**
	 * validate user token,
	 * if token is valid, set it into ThreadLocal
	 *
	 * @param token
	 * @return
	 */
	public AuthInfo validateAndBindUserToken(String token, String method, String url) {
		AuthRestBean authRestBean = validateLoginAccessToken(token);
		BeehiveJdbcUser user = authRestBean.getUser();

		PermissionTree permisssionTree = ruleService.getUserPermissionTree(user.getId());
		boolean sign = permisssionTree.doVerify(method, url);

		if (!sign) {
			throw new UnauthorizedException(UnauthorizedException.ACCESS_INVALID);
		}

		authService.bindUser(user);
		return authService.getAuthInfoByToken(token);
	}

	/**
	 * remove the token
	 *
	 * @param token
	 */
	public void logout(String token) {
		authService.removeToken(token);


	}

	private Team getTeamByID(Long userID) {
		List<Team> teamList = teamDao.findTeamByUserID(userID);
		if (teamList != null && teamList.size() > 0) {
			return teamList.get(0);
		} else {
			return null;
		}
	}

	public AuthRestBean validateUserToken(String token) {

		try {

			KiiUser kiiUser = userService.getKiiUser(token);

			String userID = kiiUser.getUserID();

			BeehiveJdbcUser beehiveUser = userDao.getUserByKiiUserID(userID);

			if (!beehiveUser.getEnable()) {

				UnauthorizedException excep = new UnauthorizedException(UnauthorizedException.USER_BEEN_LOCKED);
				excep.addParam("userName", beehiveUser.getUserName());
				throw excep;
			}
			Team team = getTeamByID(beehiveUser.getId());

			String beehiveToken = getBeehiveToken(token, token, false);

			saveToken(beehiveUser, beehiveToken, team, false);

			AuthRestBean authRestBean = generAuthBean(beehiveUser, beehiveToken, team);

			return authRestBean;
		} catch (KiiCloudException ex) {
			UnauthorizedException excep = new UnauthorizedException(UnauthorizedException.LOGIN_TOKEN_INVALID);
			excep.addParam("token", token);
			throw excep;
		}
	}
}

