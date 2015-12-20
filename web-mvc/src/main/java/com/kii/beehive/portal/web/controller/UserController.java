package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.OutputUser;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/users",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController {



	@Autowired
	private UserManager userManager;

	@RequestMapping(path="",method={RequestMethod.POST})
	public Map<String,String> createUser(@RequestBody OutputUser user){

		if(StringUtils.isEmpty(user.getUserName()) && StringUtils.isEmpty(user.getAliUserID())){
			throw new PortalException("RequiredFieldsMissing","username or userID cannot been null", HttpStatus.BAD_REQUEST);
		}

		BeehiveUser beehiveUser = user.getBeehiveUser();

		String userID = userManager.addUser(beehiveUser);

		Map<String,String> map = new HashMap<>();
		map.put("userID", userID);
		map.put("userName", user.getUserName());

		return map;
	}

	@RequestMapping(path="/{userID}",method={RequestMethod.PATCH})
	public Map<String,String> updateUser(@PathVariable("userID") String userID,@RequestBody OutputUser user){

		userManager.updateUser(user.getBeehiveUser(),userID);


		Map<String,String> map=new HashMap<>();
		map.put("userID",userID);
		return map;
	}

	@RequestMapping(path="/{userID}",method={RequestMethod.GET})
	public OutputUser getUser(@PathVariable("userID") String userID){


		return new OutputUser(userManager.getUserByID(userID));
	}


	@RequestMapping(path="/{userID}/custom",method={RequestMethod.PATCH})
	public Map<String,String> updateCustomProp(@PathVariable("userID") String userID,@RequestBody Map<String,Object> props){

		userManager.updateCustomProp(userID,props);

		Map<String,String> map=new HashMap<>();
		map.put("userID",userID);
		return map;
	}


	@RequestMapping(path="/{userID}",method={RequestMethod.DELETE},consumes={"*"})
	public void deleteUser(@PathVariable("userID") String userID){

		userManager.deleteUser(userID);

	}


	@RequestMapping(path="/simplequery",method={RequestMethod.POST})
	public List<OutputUser> queryUserByProps(@RequestBody Map<String,Object> queryMap){

		return  userManager.simpleQueryUser(queryMap).stream()
				.map((e) -> new OutputUser(e))
				.collect(Collectors.toCollection(ArrayList::new));

	}


}
