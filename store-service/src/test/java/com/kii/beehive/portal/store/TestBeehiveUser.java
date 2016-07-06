package com.kii.beehive.portal.store;


import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.PortalSyncUserManager;
import com.kii.beehive.portal.service.PortalSyncUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;

public class TestBeehiveUser extends StoreServiceTestInit {

	@Autowired
	private PortalSyncUserDao userDao;

	@Autowired
	private PortalSyncUserManager userMang;


	@Autowired
	private ObjectMapper mapper;


	@Test
	public void testUserCustomProp() throws IOException {

		BeehiveUser user=new BeehiveUser();

//		user.setCompany("threaddemo");
		user.setUserName("王二");

//		user.setCustomField("id", "1234567");
//		user.setCustomField("no", "abcdef");

//		userMang.addUser(user);
//		String json=mapper.writeValueAsString(user);
//
//System.out.println(json);
//		BeehiveUser newUser=mapper.readValue(json, BeehiveUser.class);
//
//		assertEquals(newUser.getCustomField("no"),"abcdef");

	}




}
