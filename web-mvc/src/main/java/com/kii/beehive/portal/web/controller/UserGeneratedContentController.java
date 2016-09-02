package com.kii.beehive.portal.web.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.service.UserGeneratedContentDao;
import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.UserGeneratedContent;
import com.kii.beehive.portal.web.entity.UgcData;

@RestController
@RequestMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserGeneratedContentController {


	@Autowired
	private UserGeneratedContentDao  ugcDao;




	@RequestMapping(path = "/users/me/ugc/{type}/{name}", method = {RequestMethod.GET},
			consumes = {MediaType.ALL_VALUE})
	public CustomData getCustomData(@PathVariable(value = "name") String name,@PathVariable(value="type") String type) {

		return ugcDao.getUserData(type,name, AuthInfoStore.getUserID());

	}

	@RequestMapping(path = "/users/me/ugc/{type}", method = {RequestMethod.GET} ,consumes = {MediaType.ALL_VALUE})
	public List<UgcData> getAllCustomData(@PathVariable(value="type") String type) {

		return ugcDao.getAllUserData(AuthInfoStore.getUserID(),type).stream().map(UgcData::new).collect(Collectors.toList());

	}


	@RequestMapping(path = "/users/me/ugc/{type}/{name}", method = {RequestMethod.PUT})
	public void setCustomData(@PathVariable(value = "name") String name,
							  @PathVariable(value="type") String type,
							  @RequestBody(required=false) CustomData data) {

		if(data==null){
			data=new CustomData();
		}
		
		UserGeneratedContent content=new UserGeneratedContent();
		content.setUserID(AuthInfoStore.getUserID());
		content.setName(name);
		content.setUserDataType(type);
		content.setUserData(data);

		ugcDao.setUserData(content,UserGeneratedContent.getUUID(AuthInfoStore.getUserID(),type,name));
	}


	@RequestMapping(path = "/users/me/ugc/{type}/{name}", method = {RequestMethod.DELETE},consumes = {MediaType.ALL_VALUE})
	public void deleteCustomData(@PathVariable(value = "name") String name, @PathVariable(value = "type") String type) {


		ugcDao.deleteUserData(AuthInfoStore.getUserID(),type,name);

	}



}
