package com.kii.beehive.business.ruleengine.process;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.ThingStatusChangeProcess;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(BusinessEventListenerService.REFRESH_THING_FOR_TRIGGER)
public class ThingStatusUploadProcess implements ThingStatusChangeProcess {

	@Autowired
	private EngineService engine;


	@Override
	public void onEventFire(EventListener listener, ThingStatus status, String thingID,Date timestamp) {


		engine.updateThingStatus(thingID,status,timestamp);
	}
}
