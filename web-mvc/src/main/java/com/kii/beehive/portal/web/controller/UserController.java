package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.store.entity.BeehiveUser;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/users",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {


	@Autowired
	private UserManager userManager;

    @RequestMapping(path="/",method={RequestMethod.POST})
    public Map<String,String> createUser(@RequestBody BeehiveUser user){

		String id=userManager.addUser(user);

		Map<String,String> map=new HashMap<>();
		map.put("userID",id);
		return map;
    }

    @RequestMapping(path="/{userID}",method={RequestMethod.PUT})
    public void updateUser(@RequestBody BeehiveUser user){

		userManager.updateUser(user);



    }

	@RequestMapping(path="/{userID}",method={RequestMethod.GET})
	public BeehiveUser getUser(@PathVariable("userID") String userID){


		return userManager.getUserByID(userID);



	}


    @RequestMapping(path="/{userID}/customProp",method={RequestMethod.PATCH})
    public void updateCustomProp(@PathVariable("userID") String userID,@RequestBody Map<String,Object> props){

		userManager.updateCustomProp(userID,props);
    }


	@RequestMapping(path="/{userID}",method={RequestMethod.DELETE})
	public void deleteUser(@PathVariable("userID") String userID){

		userManager.deleteUser(userID);

	}


    @RequestMapping(path="/simple-query",method={RequestMethod.POST})
    public List<BeehiveUser> queryUserByProps(@RequestBody Map<String,Object> queryMap){


		return userManager.simpleQueryUser(queryMap);

    }


}
