package com.kii.beehive.portal.store;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.manager.AppInfoManager;

public class TestAppInfoInit extends TestInit {

	@Autowired
	private AppInfoManager  manager;

	@Test
	public void testInit(){

		manager.initAppInfos("steven.jiang@kii.com","1qaz2wsx","da0b6a25");

	}
}
