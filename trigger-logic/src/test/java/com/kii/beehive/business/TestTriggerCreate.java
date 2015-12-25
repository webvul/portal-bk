package com.kii.beehive.business;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
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
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ServiceExtensionService;
import com.kii.extension.sdk.service.ThingIFService;

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

	@Autowired
	private ThingIFService  thingIFService;

	@Before
	public void init(){
		token.bindToken("qv8pBJBwDM2Tjg8fIu4jTUGwhqVsMACvtcl2Cv_teV0");
		resolver.setAppName(APP_NAME);
	}



//	@Ignore
	@Test
	public void registerExtension() throws InterruptedException, IOException {

		triggerManager.initAppForTrigger();

		extensionDao.deployScriptToApp(APP_NAME);

		InputStream reader=System.in;

		int ch=reader.read();

	}

	@Test
	public void getExtension(){
		ExtensionCodeDao.ScriptCombine json=extensionDao.getCurrentServiceCodeByVersion();

		log.info(json.getScript());

	}


	@Test
	public void callStateChange() {


		OnBoardingParam param = new OnBoardingParam();
		param.setVendorThingID("thing-test-demo-001");
		param.setUserID("f83120e36100-a83b-5e11-1eaa-026cdf52");
		param.setThingType("demo");
		param.setThingPassword("qwerty");

		OnBoardingResult result = thingIFService.onBoarding(param);

		String thingID = result.getThingID();

	}

	String  thingID="th.f83120e36100-a83b-5e11-2eaa-035b1f86";

	@Test
	public void putStatus(){

		ThingStatus status=new ThingStatus();
		status.setField("lightness",99);
		status.setField("power",true);

		thingIFService.putStatus(thingID,status);


	}

	@Test
	public void fireStateChange(){


		Map<String,Object> map=new HashMap<>();
		map.put("bucketID","_states");
		map.put("objectID","69d73330-aae2-11e5-b38a-00163e02138f");

		JsonNode node = service.callServiceExtension("global_onThingStateChange", map, JsonNode.class);

	}


	@Test
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
