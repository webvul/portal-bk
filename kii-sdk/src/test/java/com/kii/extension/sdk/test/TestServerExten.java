package com.kii.extension.sdk.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.entity.serviceextension.BucketWhenType;
import com.kii.extension.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.sdk.entity.serviceextension.HookGeneral;
import com.kii.extension.sdk.entity.serviceextension.ScheduleTriggerConfig;
import com.kii.extension.sdk.entity.serviceextension.TriggerFactory;
import com.kii.extension.sdk.entity.serviceextension.TriggerScopeType;
import com.kii.extension.sdk.service.ServiceExtensionService;
import com.kii.extension.sdk.service.UserService;

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

		EventTriggerConfig<BucketWhenType>  trigger1= TriggerFactory.getBucketInstance("lights", TriggerScopeType.Thing);
		trigger1.setWhen(BucketWhenType.DATA_OBJECT_CREATED);
		trigger1.setEndpoint("onLampAdded");

		EventTriggerConfig<BucketWhenType>  trigger2= TriggerFactory.getBucketInstance("schedules", TriggerScopeType.User);
		trigger2.setEndpoint("onScheduleAdd");
		trigger2.setWhen(BucketWhenType.DATA_OBJECT_UPDATED);

		EventTriggerConfig<BucketWhenType>  trigger3= TriggerFactory.getBucketInstance("schedules", TriggerScopeType.User);
		trigger3.setEndpoint("onScheduleUpdate");
		trigger3.setWhen(BucketWhenType.DATA_OBJECT_CREATED);

		ScheduleTriggerConfig schedule1=new ScheduleTriggerConfig();

		String json=HookGeneral.getInstance()
				.addTrggerConfig(trigger1)
				.addTrggerConfig(trigger2)
				.addTrggerConfig(trigger3)
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