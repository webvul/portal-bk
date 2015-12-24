package com.kii.beehive.business;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.kii.beehive.business.manager.BeehiveTriggerManager;
import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.service.ServiceExtensionService;

public class TestTriggerCreate extends TestTemplate  {


	@Autowired
	private BeehiveTriggerManager triggerManager;


	@Autowired
	private ExtensionCodeDao extensionDao;


	@Autowired
	private ServiceExtensionService service;

	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private ResourceLoader loader;

	@Before
	public void init(){
		resolver.setAppName("thingif");
	}

	@Test
	public void addTrigger(){

		triggerManager.initAppForTrigger();

	}

	@Test
	public void registerExtension(){

		extensionDao.deployScriptToApp("thingif");
	}

}
