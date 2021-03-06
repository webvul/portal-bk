package com.kii.beehive.business;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.extension.sdk.service.UserService;

public class BusinessTestTokenVerify extends BusinessTestTemplate {


	@Autowired
	private AppInfoManager manager;

	@Autowired
	private UserService service;



	@Test
	public void testTokenVerify(){

		String  appID="0af7a7e7";

		String token=manager.getFederatedInfo(appID).getAppAuthToken();

		assertTrue(manager.verifyAppToken(appID,token));

	}
}
