package com.kii.extension.ruleengine.sdk.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.sdk.entity.KiiUser;
import com.kii.extension.ruleengine.sdk.service.UserService;

public class TestUserAdd extends  TestTemplate {


	@Autowired
	private UserService service;



	@Before
	public void init(){

		resolver.setAppName("portal");

	}

	@Test
	public void testUserAdd(){

		KiiUser user=new KiiUser();

//		user.setCompany("demo");
		user.setLoginName("abc");
		user.setPassword("abcdef");
		user.setDisplayName("历史");

//		user.setCustomField("id", "1234567");
//		user.setCustomField("no", "abcdef");

		service.createUser(user);
	}
}
