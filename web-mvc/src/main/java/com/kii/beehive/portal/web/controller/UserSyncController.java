package com.kii.beehive.portal.web.controller;

import com.kii.beehive.business.manager.PortalSyncUserManager;
import com.kii.beehive.portal.store.entity.PortalSyncUser;
import com.kii.beehive.portal.web.entity.SyncUserRestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Beehive API - User API
 * <p>
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/usersync", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserSyncController {

	@Autowired
	private PortalSyncUserManager userManager;


	/**
	 * 创建用户
	 * POST /users
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path = "", method = {RequestMethod.POST})
	public Map<String, String> createUser(@RequestBody SyncUserRestBean user) {

		user.verifyInput();

		PortalSyncUser beehiveUser = user.getBeehiveUser();

		String userID = userManager.addUser(beehiveUser);


		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		map.put("userName", user.getUserName());

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
	public Map<String, String> updateUser(@PathVariable("userID") String userID, @RequestBody SyncUserRestBean user) {


		// clean the input user id
		user.setAliUserID(null);

		userManager.updateUser(user.getBeehiveUser(), userID);


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
	@RequestMapping(path = "/{userID}", method = {RequestMethod.GET})
	public SyncUserRestBean getUser(@PathVariable("userID") String userID) {


		return new SyncUserRestBean(userManager.getUserByID(userID));
	}

	/**
	 * 更新用户（第三方）
	 * PATCH /users/<userID>/custom
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(path = "/{userID}/custom", method = {RequestMethod.PATCH})
	public Map<String, String> updateCustomProp(@PathVariable("userID") String userID, @RequestBody Map<String, Object> props) {


		userManager.updateCustomProp(userID, props);

		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		return map;
	}

	/**
	 * 删除用户
	 * DELETE /users/<userID>
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(path = "/{userID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public void deleteUser(@PathVariable("userID") String userID) {
		userManager.deleteUser(userID);

	}

	/**
	 * 查询用户
	 * POST /users/simplequery
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param queryMap
	 */
	@RequestMapping(path = "/simplequery", method = {RequestMethod.POST})
	public List<SyncUserRestBean> queryUserByProps(@RequestBody Map<String, Object> queryMap) {

		return userManager.simpleQueryUser(queryMap).stream()
				.map((e) -> new SyncUserRestBean(e))
				.collect(Collectors.toCollection(ArrayList::new));

	}

}
