package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.event.impl.ThingStatusChangeProcess;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.service.BusinessTriggerDao;
import com.kii.beehive.portal.store.entity.BusinessTrigger;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(BusinessEventListenerService.REFRESH_THING_FOR_TRIGGER)
public class BusinessTriggerProcess implements ThingStatusChangeProcess {


	@Autowired
	private BusinessTriggerService  service;

	@Autowired
	private BusinessTriggerDao triggerDao;

	@Autowired
	private BusinessEventListenerService listenerService;


	@Override
	public void onEventFire(EventListener  listener, ThingStatus status,String thingID) {


		String listenerID=listener.getId();

		String triggerID=listener.getTargetKey();

		BusinessTrigger trigger=triggerDao.getTriggerByID(triggerID);

		if(trigger==null){
			listenerService.disableTrigger(listener.getId());
			return;
		}

		if(!trigger.getThingIDList().contains(thingID)){
			listenerService.updateThingStatusListener(trigger.getThingIDList(),listenerID);
			return;
		}

		service.onThingStateChange(triggerID,thingID,status);


	}
}
