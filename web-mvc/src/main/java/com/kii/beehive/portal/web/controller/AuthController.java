package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.AuthUtils;


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
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @return
     */
    @RequestMapping(path = "/activate", method = { RequestMethod.POST })
    public Map<String,Object> activating(@RequestBody Map<String, Object> request) {

        String userName = (String)request.get("userName");
        String password = (String)request.get("activityToken");

        if(CollectUtils.containsBlank(userName, password)) {
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userID or password empty", HttpStatus.BAD_REQUEST);
        }

        String result = authManager.activite(userName, password);

		Map<String,Object> values=new HashMap<>();
		values.put("initPwdToken",result);

		return values;

    }

	@RequestMapping(path = "/initpassword", method = { RequestMethod.POST })
	public void initPassword(@RequestBody Map<String, Object> inputMap,HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);

		if (StringUtils.isEmpty(token)) {
			throw new BeehiveUnAuthorizedException("token miss or invalid format ");
		}
		String password = (String) inputMap.get("newPassword");
		String userID = (String) inputMap.get("userName");

		authManager.initPassword(token, userID, password);
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
		if(StringUtils.isEmpty(userID)){
			userID=(String) request.get("userID");
		}
		String password = (String) request.get("password");

		if (CollectUtils.containsBlank(userID, password)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userName or password empty", HttpStatus.BAD_REQUEST);
		}

		return authManager.login(userID, password);

	}

	@RequestMapping(value = "/getTokenByID", method = {RequestMethod.POST})
	public AuthRestBean getTokenByID(@RequestBody Map<String, Object> request) {

		String userID = (String) request.get("userID");

		if (CollectUtils.containsBlank(userID)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "userName or password empty", HttpStatus.BAD_REQUEST);
		}

		return authManager.getTokenByID(userID);

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
		if(!StringUtils.isEmpty(token)) {
			authManager.logout(token);
		}else{
			throw new BeehiveUnAuthorizedException("token not existed");
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
	@RequestMapping(value = "/validatetoken", method = {RequestMethod.POST})
	public AuthRestBean validateUserToken(HttpServletRequest request) {

		String token = AuthUtils.getTokenFromHeader(request);

		if(StringUtils.isEmpty(token)){
			throw new BeehiveUnAuthorizedException("token miss or invalid format ");
		}

        return authManager.validateUserToken(token);
    }





}
