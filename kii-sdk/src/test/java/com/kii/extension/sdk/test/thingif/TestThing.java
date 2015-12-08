package com.kii.extension.sdk.test.thingif;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.factory.LocalPropertyBindTool;
import com.kii.extension.sdk.context.AdminTokenBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.LayoutPosition;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.service.ThingIFService;
import com.kii.extension.sdk.test.TestTemplate;

public class TestThing extends TestTemplate {

	@Autowired
	private ThingIFService  service;

	@Autowired
	private AppBindToolResolver bindTool;

	@Autowired
	private TokenBindToolResolver tokenResolver;

	@Before
	public void before(){

		bindTool.setAppName("test-slave-3");
	}

	@Test
	public void testThingAdd(){

		OnBoardingParam param=new OnBoardingParam();

		param.setVendorThingID("test-thing-id-00001");

		param.setLayoutPosition(LayoutPosition.STANDALONE);

		param.setThingPassword("qwerty");
		param.setThingType("demo");

		service.onBoarding(param);
	}

	private String thingID="th.f83120e36100-2cc9-5e11-b6d9-02c968ae";

	private String userID="f83120e36100-2cc9-5e11-44a9-045a59eb";
	@Test
	public void testThingSendCmd(){


		ThingCommand cmd=new ThingCommand();

		cmd.setUserID(userID);
		cmd.addMetadata("foo","bar");
		Action action=new Action();
		action.setField("power",true);
		action.setField("lightness",50);

		cmd.addAction("open",action);
		cmd.setSchema("demo");
		cmd.setSchemaVersion(0);

		service.sendCommand(thingID,cmd);
	}


}
