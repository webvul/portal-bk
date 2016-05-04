package com.kii.beehive.business;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.kii.beehive.business.manager.AppInfoManager;

public class AppInitTest extends TestInit {

	@Autowired
	private AppInfoManager appInfoManager;

	@Value("${beehive.kiicloud.dev-portal.username}")
	private String userName;

	@Value("${beehive.kiicloud.dev-portal.password}")
	private String pwd;

	@Value("${beehive.kiicloud.dev-portal.masterApp}")
	private String masterApp;
	@Test
	public void initApp(){

		appInfoManager.initAppInfos(userName,pwd,masterApp);
	}

	@Test
	public void addApp(){

		appInfoManager.addAppInfo("af5647b1",userName,pwd,masterApp);
	}
}
