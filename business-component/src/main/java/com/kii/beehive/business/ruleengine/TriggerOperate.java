package com.kii.beehive.business.ruleengine;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.ruleengine.entitys.EngineBusinessObj;
import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;

@Component
public class TriggerOperate {

	@Autowired
	private BusinessEventListenerService eventService;

	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private RuleEngineService service;

	
	@Autowired
	private EngineTriggerBuilder  triggerBuilder;
	

	public Set<String> getTriggerListByThingID(long thingID){
		
		//TODO:
		return null;
		
	}

	
	private Map<Integer,Set<EngineBusinessObj>>  dataMap=new ConcurrentHashMap<>();
	
	private AtomicInteger  index=new AtomicInteger(0);
	
	@Scheduled(initialDelay = 1000*60,fixedRate = 1000)
	public void submitData(){
		
		int oldIndex=index.getAndAccumulate(1, (left, right) -> (left+right)%10);
		
		Set<EngineBusinessObj> list=dataMap.get(oldIndex);
		
		service.updateBusinessData(list);
		
		dataMap.get(oldIndex).clear();
		
	}
	
	public void addBusinessData(BusinessDataObject data){
		
		dataMap.computeIfAbsent(index.get(),(k)-> new HashSet<>()).add(triggerBuilder.generBusinessData(data));
		
	}

	public void removeTrigger(TriggerRecord  record){


		String triggerID=record.getTriggerID();


		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			service.removeTrigger(record.getTriggerID());

			List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
			for (EventListener eventListener : eventListenerList) {
					eventListenerDao.removeEntity(eventListener.getId());
			}
		}

	}

	public void disableTrigger(TriggerRecord  record){

		String triggerID=record.getTriggerID();

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {
			service.removeTrigger(triggerID);
		}

	}
	
	
	public void createTrigger(TriggerRecord record) {
		
		EngineTrigger trigger=triggerBuilder.generEngineTrigger(record);
		
		service.addTrigger(trigger);
	}
}
