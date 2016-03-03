package com.kii.beehive.business;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.extension.ruleengine.sdk.service.UserService;

public class TestTokenVerify extends TestInit {


	@Autowired
	private AppInfoManager manager;

	@Autowired
	private UserService service;

	@Test
	public void testTokenVerify(){

		String  appID="0af7a7e7";

		String token=manager.getDefaultOwer(appID).getAppAuthToken();

		assertTrue(manager.verifyAppToken(appID,token));



	}
}
