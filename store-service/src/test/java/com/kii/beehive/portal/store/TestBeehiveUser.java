package com.kii.beehive.portal.store;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;

public class TestBeehiveUser extends TestInit{

	@Autowired
	private BeehiveUserDao  userDao;

	@Autowired
	private UserManager userMang;


	@Autowired
	private ObjectMapper mapper;


	@Test
	public void testUserCustomProp() throws IOException {

		BeehiveUser user=new BeehiveUser();

		user.setCompany("demo");
		user.setUserName("王二");

		user.setCustomField("id", "1234567");
		user.setCustomField("no", "abcdef");

		userMang.addUser(user);
//		String json=mapper.writeValueAsString(user);
//
//System.out.println(json);
//		BeehiveUser newUser=mapper.readValue(json, BeehiveUser.class);
//
//		assertEquals(newUser.getCustomField("no"),"abcdef");

	}




}
