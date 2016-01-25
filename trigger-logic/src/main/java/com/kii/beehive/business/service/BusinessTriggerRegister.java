package com.kii.beehive.business.service;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.portal.store.entity.BusinessTrigger;
import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.extension.sdk.entity.thingif.Predicate;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;

@Component
public class BusinessTriggerRegister {

	@Autowired
	private BusinessTriggerService  service;

	@Autowired
	private BusinessEventListenerService listenerService;



	public void registerBusinessTrigger(Set<String> thingIDs, String beehiveTriggerID, BeehiveTriggerType triggerType, StatePredicate predicate){


		BusinessTrigger  trigger=new BusinessTrigger();
		trigger.setCondition(predicate.getCondition());
		trigger.setThingIDList(thingIDs);
		trigger.setWhen(predicate.getTriggersWhen());

		String triggerID=service.createTrigger(trigger);

		listenerService.addBeehiveTriggerChangeListener(beehiveTriggerID,triggerID,triggerType);

	}

}
