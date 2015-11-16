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
		user.setUserName("alice");

		user.setCustomField("id", "1234567");
		user.setCustomField("no", "abcdef");

		String json=mapper.writeValueAsString(user);


		BeehiveUser newUser=mapper.readValue(json, BeehiveUser.class);

		assertEquals(newUser.getCustomField("no"),"abcdef");

	}

	@Test
	public void testEditUser(){

		Set<String> fields=new HashSet<>();
		fields.add("id");

		String userID="aba700e36100-9eca-5e11-f2c8-085371d1";

		Map<String,Object> val=userDao.getUserCustomInfoByID(userID,fields);
		assertEquals(val.get("id"),"1234567");
		assertNull(val.get("no"));

		Map<String,Object> newField=new HashMap<>();
		newField.put("id","7654321");
		newField.put("email", "abc@abc.com");

		userDao.updateUserCustomFields(userID, newField);

		fields.clear();
		fields.add("email");
		val=userDao.getUserCustomInfoByID(userID,fields);
		assertEquals("abc@abc.com",val.get("email"));



	}

	@Test
	public void addUser(){


		BeehiveUser user=new BeehiveUser();
		user.setCompany("demo");
		user.setUserName("alice");

		user.setCustomField("id", "1234567");
		user.setCustomField("no", "abcdef");

		String userID=userMang.addUser(user);

		Set<String> fields=new HashSet<>();
		fields.add("id");

		Map<String,Object> val=userDao.getUserCustomInfoByID(userID,fields);
		assertEquals(val.get("id"),"1234567");
		assertNull(val.get("no"));

		Map<String,Object> newField=new HashMap<>();
		newField.put("id","7654321");
		newField.put("email", "abc@abc.com");

		userDao.updateUserCustomFields(userID, newField);

		fields.clear();
		fields.add("email");
		val=userDao.getUserCustomInfoByID(userID,fields);
		assertEquals("abc@abc.com",val.get("email"));



	}


}
