package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.BeehiveUserManager;
import com.kii.beehive.portal.web.entity.AuthRestBean;
import com.kii.beehive.portal.web.entity.UserRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.AuthUtils;
import com.kii.extension.sdk.exception.KiiCloudException;


/**
 * Beehive API - User API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "User API" for details
 */
@RestController
@RequestMapping(value = "/oauth2", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class AuthController {

	@Autowired
	private AuthManager authManager;

	@Autowired
	private BeehiveUserManager userManager;


	public static void veifyPwd(String pwd) {

		PortalException excep = new PortalException(ErrorCode.INVALID_PASSWORD);


		if (StringUtils.isBlank(pwd)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "newPassword");

		}

		if (pwd.length() < 6) {
			throw excep;
		}


	}


	@RequestMapping(path = "/doActivity/{userID}/code/{activityCode}.do", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public RedirectView doActivity(@PathVariable("userID") String userID, @PathVariable("activityCode") String code, HttpServletRequest request) {


		String result = authManager.activiteByID(userID, code);

		String contextUrl = request.getContextPath();

		String url = contextUrl + "/initPassword.html?token=" + result;


		RedirectView view = new RedirectView(url);

		return view;

	}

	/**
	 * 用户注册
	 * POST /oauth2/register
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/activate", method = {RequestMethod.POST})
	public Map<String, Object> activating(@RequestBody Map<String, Object> request) {

		String userName = (String) request.get("userName");
		String password = (String) request.get("activityToken");

		if (StringUtils.isBlank(userName)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "userName");
		}

		if (StringUtils.isBlank(password)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "activityToken");
		}

		String result = authManager.activite(userName, password);

		Map<String, Object> values = new HashMap<>();
		values.put("initPwdToken", result);

		return values;

	}

	@RequestMapping(path = "/initpassword", method = {RequestMethod.POST})
	public void initPassword(@RequestBody Map<String, Object> inputMap, HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);

		if (StringUtils.isBlank(token)) {
			throw new PortalException(ErrorCode.INVALID_TOKEN);
		}
		String password = (String) inputMap.get("newPassword");

		veifyPwd(password);
		String userID = (String) inputMap.get("userName");

		PortalException excep = new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING);
		if (StringUtils.isBlank(userID)) {
			excep.addParam("field", "userName");
			throw excep;
		}

		authManager.initPassword(token, userID, password);
	}


	@RequestMapping(value = "/registUser", method = {RequestMethod.POST})
	public Map<String, String> createUser(@RequestBody UserRestBean user) {

		user.verifyInput();

		BeehiveJdbcUser beehiveUser = user.getBeehiveUser();

		veifyPwd(user.getPassword());

		beehiveUser.setRoleName("commUser");

		return authManager.createUserDirectly(beehiveUser, user.getPassword());

	}


	/**
	 * 用户登录
	 * POST /oauth2/login
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/login", method = {RequestMethod.POST})
	public AuthRestBean login(@RequestBody Map<String, Object> request) {

		String userID = (String) request.get("userName");
		if (StringUtils.isBlank(userID)) {
			userID = (String) request.get("userID");
		}
		String password = (String) request.get("password");

		if (CollectUtils.containsBlank(userID, password)) {

			if (StringUtils.isBlank(userID)) {
				throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "userName or userID");
			} else {
				throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "password");
			}
		}
		
		try {
			AuthUser user = authManager.login(userID, password);
			return new AuthRestBean(user);
		} catch (KiiCloudException e) {
			if ("invalid_grant".equals(e.getErrorCode())) {
				throw new PortalException(ErrorCode.PASSWORD_ERROR);
			} else {
				throw new PortalException(ErrorCode.INVALID_INPUT);
			}
		}
	
	}

	@RequestMapping(value = "/validateLoginAccessToken", method = {RequestMethod.POST})
	public AuthRestBean validateLoginAccessToken(@RequestBody String token) {
		AuthUser user=authManager.validateLoginAccessToken(token);
		return new AuthRestBean(user);
	}


	/**
	 * 用户登出
	 * POST /oauth2/logout
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/logout", method = {RequestMethod.POST},consumes=MediaType.ALL_VALUE)
	public void logout(HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);
		if (!StringUtils.isBlank(token)) {
			authManager.logout(token);
		} else {
			throw new PortalException(ErrorCode.INVALID_TOKEN);
		}
	}


	/**
	 * 验证用户（令牌）
	 * POST /oauth2/validatetoken
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/validatetoken", method = {RequestMethod.POST, RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public AuthRestBean validateUserToken(HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);

		if (StringUtils.isBlank(token)) {
			PortalException excep = new PortalException(ErrorCode.INVALID_TOKEN);
			throw excep;
		}

		AuthUser user= authManager.validateUserToken(token);

		return new AuthRestBean(user);
	}


}
