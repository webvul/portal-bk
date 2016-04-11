package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.manager.BeehiveUserManager;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.UserRestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/users", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController {


	@Autowired
	private BeehiveUserManager userManager;

	/**
	 * 创建用户
	 * POST /users
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path = "", method = {RequestMethod.POST})
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
	 * 更新用户
	 * PATCH /users/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path = "/{userID}", method = {RequestMethod.PATCH})
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
	@RequestMapping(path = "/{userID}", method = {RequestMethod.GET}, consumes = {"*"})
	public UserRestBean getUser(@PathVariable("userID") String userID) {

		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByID(userID));

		return bean;
	}

}
