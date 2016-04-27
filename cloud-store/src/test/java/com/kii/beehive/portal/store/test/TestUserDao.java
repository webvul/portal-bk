package com.kii.beehive.portal.store.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.BeehiveUserDao;

public class TestUserDao extends TestTemplate{

	//joling01

	@Autowired
	private BeehiveUserDao  userDao;

	@Test
	public void testDub(){

		userDao.getUserByName("joling01");

	}


}
