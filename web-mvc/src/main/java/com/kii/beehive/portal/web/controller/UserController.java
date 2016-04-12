package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.BeehiveUserManager;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.entity.UserRestBean;
import com.kii.beehive.portal.web.exception.PortalException;


@RestController
@RequestMapping(value = "/users", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController {


	@Autowired
	private BeehiveUserManager userManager;

	@Autowired
	private AuthManager authManager;


	/**
	 * 创建用户
	 * POST /users
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(value = "", method = {RequestMethod.POST})
	public Map<String, String> createUser(@RequestBody UserRestBean user) {

		user.verifyInput();

		BeehiveUser beehiveUser = user.getBeehiveUser();


		String token = userManager.addUser(beehiveUser);


		Map<String, String> map = new HashMap<>();

		map.put("userName", beehiveUser.getUserName());
		map.put("activityToken", token);

		return map;
	}


	/**
	 * 用户修改密码
	 * POST /oauth2/changepassword
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/changepassword", method = { RequestMethod.POST })
	public void changePassword(@RequestBody Map<String, Object> request) {

		String oldPassword = (String)request.get("oldPassword");
		String newPassord = (String)request.get("newPassword");

		if(CollectUtils.containsBlank(oldPassword, newPassord)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "oldPassword or newPassord empty", HttpStatus.BAD_REQUEST);
		}

		authManager.changePassword(oldPassword, newPassord);

	}


	/**
	 * 更新用户
	 * PATCH /users/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(value = "/{userID}", method = {RequestMethod.PATCH})
	public Map<String, String> updateUser(@PathVariable("userID") String userID, @RequestBody BeehiveUser user) {

		userManager.updateUser(user, userID);


		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		return map;
	}

	/**
	 * 通过userID查询用户
	 * GET /users/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(value = "/{userID}", method = {RequestMethod.GET}, consumes = {"*"})
	public UserRestBean getUser(@PathVariable("userID") String userID) {

		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByID(userID));

		return bean;
	}

}
