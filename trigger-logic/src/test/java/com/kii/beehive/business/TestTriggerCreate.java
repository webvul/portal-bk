package com.kii.beehive.business;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.beehive.business.manager.BeehiveTriggerManager;
import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ServiceExtensionService;

public class TestTriggerCreate extends TestTemplate  {

	private Logger log= LoggerFactory.getLogger(TestTriggerCreate.class);


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

	private String APP_NAME="portal";

	@Autowired
	private TokenBindToolResolver token;

	@Before
	public void init(){
		token.bindToken("qv8pBJBwDM2Tjg8fIu4jTUGwhqVsMACvtcl2Cv_teV0");
		resolver.setAppName(APP_NAME);
	}

	@Test
	public void addTrigger(){

		triggerManager.initAppForTrigger();

	}

	@Test
	public void registerExtension() throws InterruptedException, IOException {

		extensionDao.deployScriptToApp(APP_NAME);

		InputStream reader=System.in;

		int ch=reader.read();

	}

	@Test
	public void getExtension(){
		ExtensionCodeDao.ScriptCombine json=extensionDao.getCurrentServiceCodeByVersion();

		log.info(json.getScript());

	}


	public void callStateChange(){
//		resolver.setAppName(APP_NAME);

		Map<String,Object> map=new HashMap<>();
//		map.put("triggerID","demo");
		map.put("target","demo");

		ThingStatus  status=new ThingStatus();
		status.setField("brightness",100);

		map.put("state",status);

		JsonNode node=service.callServiceExtension("global_onThingStateChange",map,JsonNode.class);
	}


	public void callTrigger(){
//		resolver.setAppName("thingif");

		Map<String,Object> map=new HashMap<>();
		map.put("thingID","foo");
		map.put("triggerID","demo");


		String[] endpoints={"global_onPositiveTriggerArrive","global_onSummaryTriggerArrive","global_onNegitiveTriggerArrive"};

		for(String endpoint:endpoints) {
			JsonNode node = service.callServiceExtension(endpoint, map, JsonNode.class);
		}
	}


}
