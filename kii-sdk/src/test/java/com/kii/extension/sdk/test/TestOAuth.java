package com.kii.extension.sdk.test;


import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.context.AppBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.UserTokenBindTool;
import com.kii.extension.sdk.service.UserService;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.DataService;

public class TestOAuth extends TestTemplate{

	@Autowired
	private DataService dataService;

	@Autowired
	private UserService service;

	@Autowired
	private AppBindTool tool;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private AppBindToolResolver appResolver;

	@Autowired
	private UserTokenBindTool userTool;

	private String master="master-test";
	private String[] salves={"test-slave-1","test-slave-2"};



	@Test
	public void testOAuth(){


		appResolver.pushAppNameDirectly(master);
//
//		KiiUser user=new KiiUser();
//		user.setDisplayName("demo1");
//		user.setEmailAddress("demo1@foo.com");
//		user.setPassword("qwerty");

//		String userID=service.createUser(user);

//		tokenResolver.bindUser();

		userTool.bindUserInfo("threaddemo","qwerty");

		String token=appResolver.getToken();


		BucketInfo bucket=new BucketInfo("test");
		Map<String,String> val=new HashMap<>();
		val.put("foo", "bar");

		dataService.createObject(val, bucket);

		appResolver.pushAppNameDirectly(salves[0],token);

//		userTool.bindToken(token);

		dataService.createObject(val,bucket);

	}
}
