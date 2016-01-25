package com.kii.beehive.business.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.ruleengine.ExpressCompute;
import com.kii.beehive.portal.service.BusinessTriggerDao;
import com.kii.beehive.portal.store.entity.BusinessTrigger;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class BusinessTriggerService {

	@Autowired
	private BusinessEventListenerService listenerService;

	@Autowired
	private BusinessTriggerDao triggerDao;

	@Autowired
	private ExpressCompute compute;

	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private BusinessEventBus eventBus;



	public String createTrigger(BusinessTrigger trigger) {


		trigger.setEnable(false);

		for(String thingID:trigger.getThingIDList()){

			ThingStatus  status=thingIFService.getStatus(thingID);

			boolean sign=compute.doExpress(trigger.getCondition(),status.getFields());

			trigger.getMemberStates().setMemberStatus(thingID,sign);
		}

		String triggerID=triggerDao.addKiiEntity(trigger);


		listenerService.addThingStatusListenerForTrigger(trigger.getThingIDList(),triggerID);

		triggerDao.enableTrigger(triggerID);

		return triggerID;
	}

	public void onThingStateChange(String triggerID,String thingID,ThingStatus newState,String listenerID){


		BusinessTrigger trigger=triggerDao.getTriggerByID(triggerID);

		if(trigger==null){
			listenerService.disableTriggerByTargetID(triggerID);
			return;
		}

		if(!trigger.getThingIDList().contains(thingID)){
			listenerService.updateThingStatusListener(trigger.getThingIDList(),listenerID);
			return;
		}


		boolean oldSign=trigger.getMemberStates().getMemberStatus(thingID);

		Map<String,Object> result=triggerDao.executeWithVerify(triggerID,(entry)->{

			boolean sign = compute.doExpress(trigger.getCondition(), newState.getFields());

			return Collections.singletonMap(thingID,sign);

		},5);


		boolean finallySign=trigger.getWhen().checkStatus(oldSign, (Boolean) result.get(thingID));

		if(finallySign){


			eventBus.onTriggerFire(triggerID,trigger.getWhen(),thingID);


		}


	}
	

}