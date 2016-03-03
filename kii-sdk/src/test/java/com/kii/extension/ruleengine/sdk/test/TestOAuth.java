package com.kii.extension.ruleengine.sdk.test;


import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.sdk.context.AppBindTool;
import com.kii.extension.ruleengine.sdk.context.AppBindToolResolver;
import com.kii.extension.ruleengine.sdk.context.UserTokenBindTool;
import com.kii.extension.ruleengine.sdk.service.UserService;
import com.kii.extension.ruleengine.sdk.entity.BucketInfo;
import com.kii.extension.ruleengine.sdk.service.DataService;

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


		appResolver.setAppName(master);
//
//		KiiUser user=new KiiUser();
//		user.setDisplayName("demo1");
//		user.setEmailAddress("demo1@foo.com");
//		user.setPassword("qwerty");

//		String userID=service.createUser(user);

//		tokenResolver.bindUser();

		userTool.bindUserInfo("demo","qwerty");

		String token=appResolver.getToken();


		BucketInfo bucket=new BucketInfo("test");
		Map<String,String> val=new HashMap<>();
		val.put("foo", "bar");

		dataService.createObject(val, bucket);

		appResolver.setAppName(salves[0]);

		userTool.bindToken(token);

		dataService.createObject(val,bucket);

	}
}
