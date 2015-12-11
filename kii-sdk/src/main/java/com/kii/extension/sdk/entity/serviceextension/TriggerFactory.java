package com.kii.extension.sdk.entity.serviceextension;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.common.utils.StrTemplate;

public class TriggerFactory {



	private static String urlTemplate="kiicloud://${0}s/*/buckets/${1}";

	public static EventTriggerConfig<BucketWhenType> getBucketInstance(String bucketName, TriggerScopeType scope) {

		EventTriggerConfig config=new EventTriggerConfig<BucketWhenType>();

		if(scope==TriggerScopeType.App){
			config.setUrl(StrTemplate.gener(urlTemplate,"", bucketName));
		}else {
			config.setUrl(StrTemplate.gener(urlTemplate, scope.name().toLowerCase(), bucketName));
		}

		return config;
	}

	public static EventTriggerConfig<UserWhenType> getUserInstance() {

		EventTriggerConfig config=new EventTriggerConfig<UserWhenType>();

		config.setUrl("kiicloud://users");



		return config;
	}

	public static EventTriggerConfig<GroupWhenType> getGroupInstance() {

		EventTriggerConfig config=new EventTriggerConfig<GroupWhenType>();

		config.setUrl("kiicloud://groups");



		return config;
	}

	public static EventTriggerConfig<ThingWhenType> getThingInstance() {

		EventTriggerConfig config=new EventTriggerConfig<BucketWhenType>();

		config.setUrl("kiicloud://things");



		return config;
	}


	public static ScheduleTriggerConfig getScheduleInstance(String cron,String endPoint){

		ScheduleTriggerConfig config=new ScheduleTriggerConfig();
		config.setEndpoint(endPoint);
		config.setCron(cron);

		return config;
	}

}
