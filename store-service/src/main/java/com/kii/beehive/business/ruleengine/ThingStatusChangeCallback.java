package com.kii.beehive.business.ruleengine;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ThingStatusChangeCallback {

	@Autowired
	private EngineService engine;


	public void onEventFire( ThingStatus status, String thingID,Date timestamp) {


		engine.updateThingStatus(thingID,status,timestamp);
	}
}
