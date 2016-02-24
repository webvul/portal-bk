package com.kii.extension.ruleengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class StateChangeCallback {


	@Autowired
	private DroolsTriggerService  service;

	public void settingThingState(String id,ThingStatus state){
		service.addThingStatus(id,state);
	}
}
