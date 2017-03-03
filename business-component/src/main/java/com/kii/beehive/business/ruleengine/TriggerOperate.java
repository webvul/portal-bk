package com.kii.beehive.business.ruleengine;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.ruleengine.entitys.EngineBusinessObj;
import com.kii.beehive.business.ruleengine.entitys.EngineBusinessType;
import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.beehive.business.ruleengine.entitys.EngineTriggerQuery;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;

@Component
public class TriggerOperate {
	
	private Logger log = LoggerFactory.getLogger(TriggerOperate.class);

	@Autowired
	private BusinessEventListenerService eventService;

	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private RuleEngineService service;

	
	@Autowired
	private EngineTriggerBuilder  triggerBuilder;
	
	@Autowired
	private SecurityService security;
	
	
	private Map<Integer, Set<EngineBusinessObj>> dataMap = new ConcurrentHashMap<>();
	
	private AtomicInteger index = new AtomicInteger(0);
	
	public Set<String> getTriggerListByThingID(String thingID) {
		
		
		EngineTriggerQuery query = new EngineTriggerQuery();
		EngineBusinessObj obj = new EngineBusinessObj();
		obj.setBusinessID(thingID);
		obj.setGroupName(triggerBuilder.getGroupName());
		obj.setType(EngineBusinessType.Business);
		
		query.setFullObjID(obj.generFullID());
		
		Set<EngineTrigger> set = service.queryTrigger(query);
		return set.stream().map((t) -> t.getTriggerID()).collect(Collectors.toSet());
		
	}
	
	@Scheduled(initialDelay = 1000*60,fixedRate = 1000)
	public void submitData(){
		
		int oldIndex=index.getAndAccumulate(1, (left, right) -> (left+right)%10);
		
		Set<EngineBusinessObj> list=dataMap.get(oldIndex);
		
		try {
			service.updateBusinessData(list, security.getRuleEngineToken());
		} catch (TriggerServiceException e) {
			log.error(e.getMessage());
			SysMonitorMsg notice = new SysMonitorMsg();
			notice.setErrorType("SubmitDataFail");
			notice.setErrMessage(e.getMessage());
			notice.setFrom(SysMonitorMsg.FromType.RuleEngine);
			
			SysMonitorQueue.getInstance().addNotice(notice);
		}
		if (list != null) {
			list.clear();
		}
		
	}
	
	public void addBusinessData(BusinessDataObject data){
		
		dataMap.computeIfAbsent(index.get(),(k)-> new HashSet<>()).add(triggerBuilder.generBusinessData(data));
		
	}

	public void removeTrigger(String triggerID){


			service.removeTrigger(triggerID);

			List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
			for (EventListener eventListener : eventListenerList) {
					eventListenerDao.removeEntity(eventListener.getId());
			}
		

	}

	public void disableTrigger(String triggerID){

		service.disableTrigger(triggerID);

	}
	
	public void enableTrigger(String triggerID){
		service.enableTrigger(triggerID);
		
	}
	
	
	public String  createTrigger(TriggerRecord record) {
		
		EngineTrigger trigger=triggerBuilder.generEngineTrigger(record);
		
		String id=  service.addTrigger(trigger);
		record.setRelationTriggerID(id);
		
		return id;
	}
	
	public void updateTrigger(TriggerRecord record){
		
		EngineTrigger trigger=triggerBuilder.generEngineTrigger(record);
		service.updateTrigger(trigger,record.getRelationTriggerID());
	}
}
