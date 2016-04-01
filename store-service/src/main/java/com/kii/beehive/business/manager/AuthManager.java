package com.kii.beehive.business.manager;


import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.helper.AuthInfoCacheService;
import com.kii.beehive.portal.helper.AuthInfoPermanentTokenService;
import com.kii.beehive.portal.store.entity.AuthInfoEntry;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@BindAppByName(appName = "master")
@Component
public class AuthManager {

	private Logger log = LoggerFactory.getLogger(AuthManager.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AuthInfoCacheService authInfoCacheService;

	@Autowired
	private AuthInfoPermanentTokenService authInfoPermanentTokenService;

	@Autowired
	private UserTokenBindTool userTokenBindTool;

	/**
	 * register with userID and password
	 * the logic behind is as below:
	 * 1. login with the default password (generated by KiiUserSyncDao when created beehive user)
	 * 2. change password with the token from login
	 *
	 * @param userID
	 * @param password
	 */
	public boolean register(String userID, String password) {

		try {
			// TODO need to check why below was ever commented out?
			String defaultPassword = this.getDefaultPassword(userID);

			// login Kii Cloud
			LoginInfo loginInfo = userService.login(userID, defaultPassword);

			// bind token to ThreadLocal
			userTokenBindTool.bindToken(loginInfo.getToken());

			// change from default password to new password
			userService.changePassword(defaultPassword, password);

		} catch (KiiCloudException e) {
			log.debug("Login with default password failed", e);
			return false;
		}

		return true;
	}

	private String getDefaultPassword(String userID) {
		return DigestUtils.sha1Hex(userID + "_beehive");
	}

	/**
	 * login Kii Cloud and save the auth info
	 *
	 * @param userID
	 * @param password
	 * @param permanentToken true: save auth info into permanent token cache and DB;
	 *                       false: save auth info into auth info cache only
	 * @return
	 */
	public LoginInfo login(String userID, String password, boolean permanentToken) {

		// login Kii Cloud
		LoginInfo loginInfo = null;

		try {
			loginInfo = userService.login(userID, password);
		} catch (KiiCloudException e) {
			log.debug("Login failed", e);
			return null;
		}


		// if permanent token is required, save the auth info into permanent token cache and DB;
		// else, save the auth info into auth info cache
		AuthInfoEntry authInfoEntry = null;
		if (permanentToken) {
			authInfoEntry = authInfoPermanentTokenService.saveToken(userID, loginInfo.getToken());
		} else {
			authInfoEntry = authInfoCacheService.saveToken(userID, loginInfo.getToken());
		}

		return loginInfo;
	}

	/**
	 * change the Kii user password in Kii Cloud;
	 * if success, remove the token
	 *
	 * @param oldPassword
	 * @param newPassword
	 */
	public void changePassword(String oldPassword, String newPassword) {

		userService.changePassword(oldPassword, newPassword);

		// remove the auth info from auth info cache
		String token = userTokenBindTool.getToken();
		authInfoCacheService.removeToken(token);

		// remove the auth info from permanent token cache and DB
		String userID = AuthInfoStore.getUserID();
		this.removePermanentToken(userID);
	}

	/**
	 * remove token from permanent token cache and DB
	 *
	 * @param userID
	 */
	private void removePermanentToken(String userID) {

		// remove token from DB
		List<String> tokenList = authInfoPermanentTokenService.removeTokenFromDBByUserID(userID);

		// remove token from cache
		for (String token : tokenList) {
			authInfoPermanentTokenService.removeTokenFromCache(token);
		}

	}

	/**
	 * validate user token,
	 * if token is valid, set it into ThreadLocal
	 *
	 * @param token
	 * @return
	 */
	public AuthInfoEntry validateAndBindUserToken(String token) throws UnauthorizedException {

		// try to get auth info from auth info cache by token
		AuthInfoEntry authInfo = authInfoCacheService.getAuthInfo(token);

		// if auth info not found in auth info cache, try to get it from permanent token cache or DB
		if (authInfo == null) {
			authInfo = authInfoPermanentTokenService.getAuthInfo(token);
		}

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

		// remove token from auth info cache
		authInfoCacheService.removeToken(token);

		// remove token from permanent token cache and DB
		authInfoPermanentTokenService.removeToken(token);
	}

	/**
	 * clean the user token in ThreadLocal
	 */
	public void unbindUserToken() {
		userTokenBindTool.clean();
	}

	/**
	 * get auth info entry by token <br/>
	 * this method is supposed to be called after AuthInterceptor validated the token
	 *
	 * @param token
	 * @return null if the corresponding auth info entry doesn't exist
	 */
	public AuthInfoEntry getAuthInfoEntry(String token) {
		if (Strings.isBlank(token)) {
			return null;
		}

		AuthInfoEntry authInfoEntry = authInfoCacheService.getAuthInfo(token);

		if (authInfoEntry == null) {
			authInfoEntry = authInfoPermanentTokenService.getAuthInfo(token);
		}

		return authInfoEntry;
	}

}

