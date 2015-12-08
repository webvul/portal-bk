package com.kii.extension.sdk.test.thingif;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.factory.LocalPropertyBindTool;
import com.kii.extension.sdk.context.AdminTokenBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.LayoutPosition;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.service.ThingIFService;
import com.kii.extension.sdk.test.TestTemplate;

public class TestThing extends TestTemplate {

	@Autowired
	private ThingIFService  service;

	@Autowired
	private AppBindToolResolver bindTool;

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


}
