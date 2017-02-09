package com.kii.beehive.business.ruleengine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.TriggerCreateException;
import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;
import com.kii.extension.ruleengine.service.BusinessObjDao;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;

@Component
public class TriggerOperate {

	@Autowired
	private BusinessEventListenerService eventService;



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private BeehiveTriggerService general;
	
	
	@Autowired
	private BusinessObjDao businessObjDao;
	
	@Autowired
	private TriggerConvertTool convertTool;


	public Set<String> getTriggerListByThingID(long thingID){
	
//		String fullKiiThingID=thingTagService.getThingByID(thingID).getFullKiiThingID();
		
		return general.getTriggerIDByObjID(BusinessObjType.Thing.getFullID(String.valueOf(thingID),null));
		
		
	}

	public List<String> init(List<TriggerRecord> list){


		List<String> errList=new ArrayList<>();

		general.enterInit();


		for(TriggerRecord  trigger:list){

			try {
				createTrigger(trigger,false);
				
			}catch(TriggerCreateException ex){
				errList.add(trigger.getTriggerID());
			}

		}


		thingTagService.iteratorAllThingsStatus(s -> {
			if (org.springframework.util.StringUtils.isEmpty(s.getStatus())) {
				return;
			}

			BusinessObjInRule info = new BusinessObjInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(s.getStatus());
			
			BusinessDataObject obj=new BusinessDataObject();
			obj.setBusinessType(BusinessObjType.Thing);
			obj.setBusinessObjID(String.valueOf(s.getId()));
			obj.setData(s.getStatus());
			obj.setModified(s.getModifyDate());

			general.updateBusinessData(obj);
		});

		businessObjDao.getAllBusinessObjs().forEach((obj)->{
			
			general.updateBusinessData(obj);

		});


		general.leaveInit();

		return errList;

	}
	
	
	public void addBusinessData(BusinessDataObject data){
		businessObjDao.addBusinessObj(data);
		
		general.updateBusinessData(data);
	}

	
	
	public void createTrigger(TriggerRecord record,boolean fireNow) throws TriggerCreateException {

		String triggerID=record.getId();
		
		if (record instanceof GroupTriggerRecord) {
			GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
			
			record=convertTool.convertGroup(groupRecord);
			
		} else if (record instanceof SummaryTriggerRecord) {
			SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;
			
			record=convertTool.convertSummary(summaryRecord);
		}
		
		try {
	
			Map<String,Set<String>> map=new HashMap<>();


			if (record instanceof SimpleTriggerRecord) {
				
				map.put("comm",Collections.singleton(((SimpleTriggerRecord) record).getSource().getBusinessObj().getFullID()));

			} else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;
				
				map=addMulToEngine(multipleRecord);
				
				multipleRecord.getSummarySource().forEach((k, v) -> {
					if(v instanceof GroupSummarySource) {
						ThingCollectSource collect=((GroupSummarySource)v).getSource();
						if(collect.getSelector()!=null&&!collect.getSelector().getTagList().isEmpty()) {
							eventService.addSummaryTagChangeListener(collect.getSelector().getTagList(), triggerID, k);
						}
					}
				});

			}else{
				throw new InvalidTriggerFormatException("unsupport trigger type");

			}
			
			general.addTriggerToEngine(record,map,fireNow);
			

		} catch (TriggerCreateException e) {
			throw e;
		}catch(RuntimeException ex){
			throw new TriggerCreateException("unknown exception",ex);
		}

	}


	public void removeTrigger(TriggerRecord  record){


		String triggerID=record.getTriggerID();


		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			general.removeTrigger(record.getTriggerID());

			List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
			for (EventListener eventListener : eventListenerList) {
					eventListenerDao.removeEntity(eventListener.getId());
			}
		}

	}

	public void disableTrigger(TriggerRecord  record){

		String triggerID=record.getTriggerID();

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {



			general.removeTrigger(record.getTriggerID());
		}

	}



	public Map<String, Object> getRuleEngingDump(String triggerID) {

		return general.getRuleEngingDump(triggerID);
	}



	private Map<String, Set<String>> addMulToEngine(MultipleSrcTriggerRecord record) {
		Map<String, Set<String>> thingMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			switch(v.getType()){
				case thing:
					ThingSource thing=(ThingSource)v;
					thingMap.put(k, Collections.singleton(thing.getBusinessObj().getFullID()));
					break;
				case summary:
					GroupSummarySource summary=(GroupSummarySource)v;
					
					thingMap.put(k,getBusinessObjSet(summary.getSource()));
					break;
			}
		});

		return thingMap;


	}
	
	private Set<String>  getBusinessObjSet(ThingCollectSource source){
		
		if(source.getSelector().notEmpty()){
			
			return thingTagService.getBusinessObjs(source.getSelector());
			
		}else {
			
			return source.getFullBusinessObjs().stream().map(BusinessDataObject::getFullID).collect(Collectors.toSet());
		}
	}
	
	
}
