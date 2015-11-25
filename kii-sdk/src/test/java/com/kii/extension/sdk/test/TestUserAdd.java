package com.kii.extension.sdk.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.service.UserService;

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
