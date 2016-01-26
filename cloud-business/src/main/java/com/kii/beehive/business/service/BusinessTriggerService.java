package com.kii.beehive.business.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.ruleengine.ExpressCompute;
import com.kii.beehive.portal.service.BusinessTriggerDao;
import com.kii.beehive.portal.store.entity.BusinessTrigger;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class BusinessTriggerService {


	@Autowired
	private BusinessTriggerDao triggerDao;

	@Autowired
	private ExpressCompute compute;

	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private BusinessEventBus eventBus;

	@Autowired
	private BusinessEventListenerService listenerService;


	private String createTrigger(BusinessTrigger trigger,Collection<String> thingIDs) {


		trigger.setEnable(false);

		for(String thingID:thingIDs){

			ThingStatus  status=thingIFService.getStatus(thingID);

			boolean sign=compute.doExpress(trigger.getCondition(),status.getFields());

			trigger.getMemberStates().setMemberStatus(thingID,sign);
		}

		String triggerID=triggerDao.addKiiEntity(trigger);


		listenerService.addThingStatusListenerForTrigger(trigger.getThingIDSet(),triggerID);

		triggerDao.enableTrigger(triggerID);

		return triggerID;
	}

	public void onThingStateChange(String triggerID,String thingID, ThingStatus newState){


		BusinessTrigger trigger=triggerDao.getTriggerByID(triggerID);

		boolean oldSign=trigger.getMemberStates().getMemberStatus(thingID);

		Map<String,Object> result=triggerDao.executeWithVerify(triggerID,(entry)->{

			boolean sign = compute.doExpress(trigger.getCondition(), newState.getFields());

			return Collections.singletonMap(thingID,sign);

		},5);


		boolean newSign= (boolean) result.get(thingID);

		boolean finallySign=trigger.getWhen().checkStatus(oldSign, newSign);

		if(finallySign){
			eventBus.onTriggerFire(triggerID,trigger.getWhen(),thingID,newSign);
		}


	}

	public BusinessTrigger registerBusinessTrigger(Collection<String> thingIDs, String targetID, StatePredicate predicate){


		BusinessTrigger  trigger=new BusinessTrigger();
		trigger.setCondition(predicate.getCondition());
		trigger.setWhen(predicate.getTriggersWhen());
		trigger.setTargetID(targetID);


		String triggerID=createTrigger(trigger,thingIDs);

		String listenerID=listenerService.addBeehiveTriggerChangeListener(targetID,triggerID);

		triggerDao.addListenerID(listenerID,triggerID);

		return trigger;

	}


	public void removeTrigger(String triggerID) {


		BusinessTrigger trigger=triggerDao.getTriggerByID(triggerID);
		if(trigger==null){
			return;
		}
		triggerDao.removeEntity(triggerID);

		if(trigger!=null) {
			listenerService.removeListener(trigger.getListenerID());
		}
	}
	


	public BusinessTrigger updateTrigger(Collection<String> newThingList,String triggerID){

		triggerDao.executeWithVerify(triggerID,trigger->{

			Set<String> newThings=new HashSet<>(newThingList);

			newThings.removeAll(trigger.getThingIDSet());

			Map<String,Boolean>  newStates=trigger.getMemberStates().getMemberStatusMap();
			newStates.replaceAll((k,v)-> null);

			for(String thingID:newThings){

				ThingStatus  status=thingIFService.getStatus(thingID);

				boolean sign=compute.doExpress(trigger.getCondition(),status.getFields());

				newStates.put(thingID,sign);
			}


			Map<String,Object> param=new HashMap<>();

			param.put("thingIDList",newStates.keySet());

			param.putAll(newStates);

			return param;

		},5);

		BusinessTrigger trigger=triggerDao.getTriggerByID(triggerID);

		listenerService.updateThingStatusListener(newThingList,trigger.getListenerID());

		return trigger;
	}
	
	
	public BusinessTrigger getTriggerByID(String triggerID) {

		return triggerDao.getTriggerByID(triggerID);
	}
}