package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventProcess;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(BusinessEventListenerService.REFRESH_THING_FOR_TRIGGER)
public class BusinessTriggerProcess implements BusinessEventProcess {


	@Autowired
	private BusinessTriggerService  service;

	@Override
	public void onEventFire(EventListener  listener, EventParam param) {

		ThingStatus status= (ThingStatus) param.getParam("status");

		String thingID= (String) param.getParam("thingID");


		service.onThingStateChange(listener.getTargetKey(),thingID,status,listener.getId());
	}
}
