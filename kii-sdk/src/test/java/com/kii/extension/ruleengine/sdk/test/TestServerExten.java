package com.kii.extension.ruleengine.sdk.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.sdk.entity.serviceextension.BucketWhenType;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.ScheduleTriggerConfig;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.TriggerScopeType;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.HookGeneral;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.TriggerFactory;
import com.kii.extension.ruleengine.sdk.service.ServiceExtensionService;

public class TestServerExten extends TestTemplate{


	@Autowired
	private ServiceExtensionService service;


	@Autowired
	private ObjectMapper mapper;

	@Before
	public void init(){

		resolver.setAppName("test-slave-1");

	}

	@Test
	public void testHookGeneral(){

//
//		{
// "kiicloud://users/*/buckets/lights":[
//		{
//			"when":"DATA_OBJECT_CREATED",
//				"what":"EXECUTE_SERVER_CODE",
//				"endpoint":"onLampAdded"
//		}
//		],
//
//		"kiicloud://users/*/buckets/schedules":[
//		{
//			"when":"DATA_OBJECT_CREATED",
//				"what":"EXECUTE_SERVER_CODE",
//				"endpoint":"onScheduleAdd"
//		},
//		{
//			"when":"DATA_OBJECT_UPDATED",
//				"what":"EXECUTE_SERVER_CODE",
//				"endpoint":"onScheduleUpate"
//		}
//		],
//
//		"kiicloud://scheduler":{
//			"globe_schedule":{
//				"cron":"0/1 * * * *",
//						"endpoint":"onEveryMinute",
//						"what":"EXECUTE_SERVER_CODE"
//			},
//			"manager_hourly":{
//				"cron":"0 0/6 * * *",
//						"endpoint":"onSixlyHour",
//						"what":"EXECUTE_SERVER_CODE"
//			},
//			"manager_daily":{
//				"cron":"0 1 * * *",
//						"endpoint":"onDaily",
//						"what":"EXECUTE_SERVER_CODE"
//			},
//
//		}
//	}
//

		EventTriggerConfig trigger1= TriggerFactory.getBucketInstance("lights", BucketWhenType.DATA_OBJECT_CREATED, TriggerScopeType.Thing);
		trigger1.setEndpoint("onLampAdded");

		EventTriggerConfig  trigger2= TriggerFactory.getBucketInstance("schedules", BucketWhenType.DATA_OBJECT_UPDATED,TriggerScopeType.User);
		trigger2.setEndpoint("onScheduleAdd");

		EventTriggerConfig  trigger3= TriggerFactory.getBucketInstance("schedules",BucketWhenType.DATA_OBJECT_CREATED, TriggerScopeType.User);
		trigger3.setEndpoint("onScheduleUpdate");

		ScheduleTriggerConfig schedule1=new ScheduleTriggerConfig();


		String json=HookGeneral.getInstance()
				.addTriggerConfig(trigger1)
				.addTriggerConfig(trigger2)
				.addTriggerConfig(trigger3)
				.addTriggerConfig(TriggerFactory.getScheduleInstance("0/1 * * * *","onEveryMinute"))
				.addTriggerConfig(TriggerFactory.getScheduleInstance("0 0/6 * * *","onSixlyHour"))
				.generJson(mapper);

		System.out.println(json);


	}

	@Test
	public void deployServiceExtension(){




	}

	@Test
	public void deployServiceExtensionWithHook(){

	}

}
