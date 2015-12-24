package com.kii.extension.sdk.entity.serviceextension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.tools.doclint.Messages;

import com.kii.beehive.portal.common.utils.StrTemplate;

public class TriggerFactory {



	private static String urlTemplate="kiicloud://${0}buckets/${1}";

	public static EventTriggerConfig getBucketInstance(String bucketName,BucketWhenType type, TriggerScopeType scope) {

		EventTriggerConfig config=new EventTriggerConfig();

		config.setWhen(WhenType.valueOf(type.name()));
		if(scope==TriggerScopeType.App){
			config.setUrl(StrTemplate.gener(urlTemplate,"", bucketName));
		}else {
			config.setUrl(StrTemplate.gener(urlTemplate, scope.name().toLowerCase()+"/*/", bucketName));
		}

		return config;
	}

	public static EventTriggerConfig getUserInstance(UserWhenType type) {

		EventTriggerConfig config=new EventTriggerConfig();

		config.setUrl("kiicloud://users");

		config.setWhen(WhenType.valueOf(type.name()));


		return config;
	}

	public static EventTriggerConfig getGroupInstance(GroupWhenType type) {

		EventTriggerConfig config=new EventTriggerConfig();

		config.setUrl("kiicloud://groups");

		config.setWhen(WhenType.valueOf(type.name()));


		return config;
	}

	public static EventTriggerConfig getThingInstance(ThingWhenType type) {

		EventTriggerConfig config=new EventTriggerConfig();

		config.setUrl("kiicloud://things");

		config.setWhen(WhenType.valueOf(type.name()));


		return config;
	}


	public static ScheduleTriggerConfig getScheduleInstance(String cron,String endPoint){

		ScheduleTriggerConfig config=new ScheduleTriggerConfig();
		config.setEndpoint(endPoint);
		config.setCron(cron);

		return config;
	}

}
