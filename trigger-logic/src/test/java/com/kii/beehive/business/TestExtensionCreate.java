package com.kii.beehive.business;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.beehive.business.manager.BeehiveTriggerInitManager;
import com.kii.beehive.business.service.ServiceExtensionDeployService;
import com.kii.beehive.portal.service.ExtensionCodeDao;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ServiceExtensionService;
import com.kii.extension.sdk.service.ThingIFService;

public class TestExtensionCreate extends TestTemplate  {

	private Logger log= LoggerFactory.getLogger(TestExtensionCreate.class);


	@Autowired
	private BeehiveTriggerInitManager triggerManager;


	@Autowired
	private ServiceExtensionDeployService extensionService;



	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private ResourceLoader loader;

	private String APP_NAME="b8ca23d0";

	@Autowired
	private TokenBindToolResolver token;

	@Autowired
	private ThingIFService  thingIFService;

	@Before
	public void init(){
		resolver.setAppName(APP_NAME);
	}



//	@Ignore
	@Test
	public void registerExtension() throws InterruptedException, IOException {

		triggerManager.initAppForTrigger();

		extensionService.deployScriptToApp(APP_NAME);

		InputStream reader=System.in;

		int ch=reader.read();

	}

	@Test
	public void deployExtensionToAll() throws IOException {

		triggerManager.deployTriggerToAll();

		InputStream reader=System.in;

		int ch=reader.read();
	}

	@Test
	public void getExtension(){
		ServiceExtensionDeployService.ScriptCombine json=extensionService.getCurrentServiceCodeByVersion(APP_NAME);

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
		map.put("objectID","75bb9560-ae07-11e5-b38a-00163e02138f");

		JsonNode node = extensionService.callServiceExtension(APP_NAME,"global_onThingStateChange", map, JsonNode.class);

	}


	@Test
	public void callTrigger(){
//		resolver.setAppName("thingif");

		Map<String,Object> map=new HashMap<>();
		map.put("thingID","foo");
		map.put("triggerID","demo");


		String[] endpoints={"global_onSimpleTriggerArrive","global_onPositiveTriggerArrive","global_onSummaryTriggerArrive","global_onNegitiveTriggerArrive"};

		for(String endpoint:endpoints) {
			JsonNode node = extensionService.callServiceExtension(APP_NAME,endpoint, map, JsonNode.class);
		}
	}


}
