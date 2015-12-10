package com.kii.extension.sdk.entity.serviceextension;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.common.utils.StrTemplate;

public class EventTriggerConfig<E extends Enum> extends  TriggerConfig {


	private E when;


	private String endpoint;

	private String url;



	public String  getUrl(){

		return url;
	}

	public E getWhen() {
		return when;
	}

	public void setWhen(E when) {
		this.when = when;
	}



	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}


	private static String urlTemplate="kiicloud://${0}s/*/buckets/${1}";

	@JsonIgnore
	public static EventTriggerConfig<BucketWhenType> getBucketInstance(String bucketName, TriggerScopeType scope) {

		EventTriggerConfig config=new EventTriggerConfig<BucketWhenType>();

		if(scope==TriggerScopeType.App){
			config.url = StrTemplate.gener(urlTemplate,"", bucketName);
		}else {
			config.url = StrTemplate.gener(urlTemplate, scope.name().toLowerCase(), bucketName);
		}

		return config;
	}

	@JsonIgnore
	public static EventTriggerConfig<UserWhenType> getUserInstance() {

		EventTriggerConfig config=new EventTriggerConfig<UserWhenType>();

		config.url = "kiicloud://users";

		return config;
	}

	@JsonIgnore
	public static EventTriggerConfig<GroupWhenType> getGroupInstance() {

		EventTriggerConfig config=new EventTriggerConfig<GroupWhenType>();

		config.url = "kiicloud://groups";

		return config;
	}

	@JsonIgnore
	public static EventTriggerConfig<ThingWhenType> getThingInstance() {

		EventTriggerConfig config=new EventTriggerConfig<BucketWhenType>();

		config.url = "kiicloud://things";

		return config;
	}
}
