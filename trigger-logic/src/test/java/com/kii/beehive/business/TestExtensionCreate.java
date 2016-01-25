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

import com.kii.beehive.business.service.ServiceExtensionDeployService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.TriggerMaintainManager;
import com.kii.beehive.portal.service.BeehiveParameterDao;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ThingIFService;

public class TestExtensionCreate extends TestTemplate  {

	private Logger log= LoggerFactory.getLogger(TestExtensionCreate.class);


	@Autowired
	private TriggerMaintainManager triggerManager;


	@Autowired
	private ServiceExtensionDeployService extensionService;


	@Autowired
	private ThingIFInAppService thingIFInAppService;

	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private ResourceLoader loader;

	private String APP_NAME="b8ca23d0";

	private String APP_NAME1="c1744915";

	private String APP_NAME2="0af7a7e7";

	@Autowired
	private TokenBindToolResolver token;

	@Autowired
	private ThingIFService  thingIFService;

	@Before
	public void init(){
		resolver.setAppName(APP_NAME);
	}


	@Autowired
	private BeehiveParameterDao parameterDao;


	@Test
	public void initParameter(){


		CallbackUrlParameter param=new CallbackUrlParameter();

		param.setBaseUrl("http://114.215.196.178:7070/api/echo");


		param.setStateChange("stateChangeCallback");

		parameterDao.saveTriggerCallbackParam(APP_NAME,param);
	}

	@Test
	public void registerExtension() throws InterruptedException, IOException {

		triggerManager.initAppForTrigger();

		extensionService.deployScriptToApp(APP_NAME1);

		InputStream reader=System.in;

		int ch=reader.read();

	}

	@Test
	public void deployExtensionToAll() throws IOException {

//		triggerManager.deployTriggerToAll();

		InputStream reader=System.in;

		int ch=reader.read();
	}

	@Test
	public void getExtension(){
		ServiceExtensionDeployService.ScriptCombine json=extensionService.getCurrentServiceCodeByVersion(APP_NAME2);

		log.info(json.getScript());

	}

	@Autowired
	private GlobalThingSpringDao  thingDao;

	@Test
	public void createThing(){

//		thingIFInAppService.removeThing(param);

//		String vendorThingID="a1b2c3d4e5f6"+System.currentTimeMillis();

		String vendorThingID="a1b2c3d4e5f61";

//		GlobalThingInfo info=new GlobalThingInfo();
//		info.setKiiAppID(APP_NAME1);
//		String fullKiiThingID= ThingIDTools.joinFullKiiThingID(thingID,APP_NAME);

//		info.setFullKiiThingID(fullKiiThingID);

//		info.setVendorThingID(vendorThingID);
//		info.setType("demo");
//
//		long id=thingDao.insert(info);


		OnBoardingParam param = new OnBoardingParam();
		param.setVendorThingID(vendorThingID);
		param.setThingPassword("password");

		String thingID=thingIFInAppService.onBoarding(param,APP_NAME2).getThingID();

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

	String  thingID=APP_NAME2+"-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

	@Test
	public void putStatus(){

		ThingStatus status=new ThingStatus();
		status.setField("lightness",99);
		status.setField("power",true);

		thingIFInAppService.putStatus(thingID,status);


	}

	@Test
	public void fireStateChange(){


		Map<String,Object> map=new HashMap<>();
		map.put("triggerID","4318c680-af80-11e5-962a-00163e02138f");
		map.put("thingID","th.aba700e36100-b2f8-5e11-70ea-04a2bfe3");

		JsonNode node = extensionService.callServiceExtension(APP_NAME,"global_onSimpleTriggerArrive", map, JsonNode.class);

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
