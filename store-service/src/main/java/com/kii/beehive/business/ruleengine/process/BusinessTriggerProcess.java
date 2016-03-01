package com.kii.beehive.business.ruleengine.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.ThingStatusChangeProcess;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.service.BusinessTriggerDao;
import com.kii.beehive.portal.store.entity.trigger.BusinessTrigger;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(BusinessEventListenerService.REFRESH_THING_FOR_TRIGGER)
public class BusinessTriggerProcess implements ThingStatusChangeProcess {



	@Autowired
	private BusinessTriggerDao triggerDao;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Autowired
	private EngineService engine;


	@Override
	public void onEventFire(EventListener  listener, ThingStatus status,String thingID) {


		String listenerID=listener.getId();

		String triggerID=listener.getTargetKey();

		BusinessTrigger trigger=triggerDao.getTriggerByID(triggerID);

		if(trigger==null){
			listenerService.disableTrigger(listener.getId());
			return;
		}

		if(!trigger.getThingIDSet().contains(thingID)){
			listenerService.updateThingStatusListener(trigger.getThingIDSet(),listenerID);
			return;
		}



	}
}
