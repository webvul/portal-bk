package com.kii.beehive.business;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.StoreServiceTestInit;

public class TestAppInfoInit extends StoreServiceTestInit {

	@Autowired
	private AppInfoManager manager;

	@Test
	public void testInit(){

		manager.initAppInfos("steven.jiang@kii.com","1qaz2wsx","da0b6a25");

	}
}
