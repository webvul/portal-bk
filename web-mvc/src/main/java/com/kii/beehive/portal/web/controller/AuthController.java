package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

	/**
	 * 用户注册
	 * POST /oauth2/register
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/register", method = {RequestMethod.POST})
	public void activity(@RequestBody Map<String, Object> request) {

		String userID = (String) request.get("userID");
		String password = (String) request.get("activityToken");

		if (CollectUtils.containsBlank(userID, password)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
		}

		boolean result = authManager.activity(userID, password);

		if (result == false) {
			throw new PortalException(ErrorCode.AUTH_FAIL, "userID incorrect or already registered", HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/initpassword", method = {RequestMethod.POST})
	public void initPassword(@RequestBody Map<String, Object> request) {

		String password = (String) request.get("newPassword");
		String userID = (String) request.get("userID");

		authManager.initPassword(userID, password);

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

		String userID = (String) request.get("userID");
		String password = (String) request.get("password");
		Boolean permanentToken = (Boolean) request.get("permanentToken");
		// if permanentToken is not set, make it false as default
		if (permanentToken == null) {
			permanentToken = false;
		}

		if (CollectUtils.containsBlank(userID, password)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
		}

		return authManager.login(userID, password, permanentToken);

	}

	/**
	 * 用户登出
	 * POST /oauth2/logout
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/logout", method = {RequestMethod.POST})
	public void logout(HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);
		authManager.logout(token);

	}


	/**
	 * 验证用户（令牌）
	 * POST /oauth2/validatetoken
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/validatetoken", method = {RequestMethod.POST})
	public AuthRestBean validateUserToken(HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);

		return authManager.validateUserToken(token);
	}


	/**
	 * 用户修改密码
	 * POST /oauth2/changepassword
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(value = "/changepassword", method = {RequestMethod.POST})
	public void changePassword(@RequestBody Map<String, Object> request) {

		String oldPassword = (String) request.get("oldPassword");
		String newPassord = (String) request.get("newPassword");

		if (CollectUtils.containsBlank(oldPassword, newPassord)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "oldPassword or newPassord empty", HttpStatus.BAD_REQUEST);
		}

		authManager.changePassword(oldPassword, newPassord);

	}


}
