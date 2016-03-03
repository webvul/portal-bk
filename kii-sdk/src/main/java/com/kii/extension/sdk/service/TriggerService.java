package com.kii.extension.sdk.service;

import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class TriggerService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	@Autowired
	private TokenBindToolResolver tool;


	private ApiAccessBuilder getBuilder(){
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info).bindToken(bindToolResolver.getToken());
	}

	public String createTrigger(String thingID, ThingTrigger trigger){
		HttpUriRequest request=getBuilder().createTrigger(thingID,trigger).generRequest(mapper);

		Map<String,Object> map=client.executeRequestWithCls(request,Map.class);

		return (String) map.get("triggerID");

	}

	public ThingTrigger getTrigger(String thingID,String triggerID){
		HttpUriRequest request=getBuilder().getTrigger(thingID,triggerID).generRequest(mapper);

		ThingTrigger trigger=client.executeRequestWithCls(request,ThingTrigger.class);

		return trigger;
	}

	public void updateTrigger(String thingID,String triggerID,ThingTrigger trigger){
		HttpUriRequest request=getBuilder().updateTrigger(thingID,triggerID,trigger).generRequest(mapper);

		client.executeRequest(request);

		return;
	}

	public void deleteTrigger(String thingID,String triggerID){
		HttpUriRequest request=getBuilder().deleteTrigger(thingID,triggerID).generRequest(mapper);

		client.executeRequest(request);

		return;
	}
}
