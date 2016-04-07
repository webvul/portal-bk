package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.manager.UserManager;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamUserRelation;
import com.kii.beehive.portal.web.entity.SyncUserRestBean;
import com.kii.beehive.portal.web.entity.UserRestBean;

@RestController
@RequestMapping(path = "/users",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController {



	@Autowired
	private UserManager userManager;

	/**
	 * 创建用户
	 * POST /users
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path="",method={RequestMethod.POST})
	public Map<String,String> createUser(@RequestBody UserRestBean user){

		user.verifyInput();

		BeehiveUser beehiveUser = user.getBeehiveUser();

		beehiveUser = userManager.addUser(beehiveUser);


		Map<String,String> map = new HashMap<>();

		map.put("userName", beehiveUser.getUserName());
		map.put("activityToken",beehiveUser.getActivityToken());

		return map;
	}

	/**
	 * 更新用户
	 * PATCH /users/{userID}
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(path="/{userID}",method={RequestMethod.PATCH})
	public Map<String,String> updateUser(@PathVariable("userID") String userID, @RequestBody SyncUserRestBean user){

		checkTeam(userID);

		// clean the input user id
		user.setAliUserID(null);

		userManager.updateUser(user.getBeehiveUser(),userID);


		Map<String,String> map=new HashMap<>();
		map.put("userID",userID);
		return map;
	}

	/**
	 * 通过userID查询用户
	 * GET /users/{userID}
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(path="/{userID}",method={RequestMethod.GET})
	public SyncUserRestBean getUser(@PathVariable("userID") String userID){

		checkTeam(userID);

		return new SyncUserRestBean(userManager.getUserByID(userID));
	}

	private void checkTeam(String userID){
		if(isTeamIDExist()){
			TeamUserRelation tur = teamUserRelationDao.findByTeamIDAndUserID(this.getLoginTeamID(), userID);
			if(tur == null){
				throw new PortalException("User Not Found", "userID:" + userID + " Not Found", HttpStatus.NOT_FOUND);
			}
		}
	}

}
