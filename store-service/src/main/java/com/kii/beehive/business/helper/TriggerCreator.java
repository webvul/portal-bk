package com.kii.beehive.business.helper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.TriggerCreateException;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;

@Component
public class TriggerCreator {

	@Autowired
	private BusinessEventListenerService eventService;



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private BeehiveTriggerService general;



	public List<String> init(List<TriggerRecord> list){


		List<String> errList=new ArrayList<>();

		general.enterInit();


		for(TriggerRecord  trigger:list){

			try {
				createTrigger(trigger);
			}catch(TriggerCreateException ex){
				errList.add(trigger.getTriggerID());
			}

		}


		thingTagService.iteratorAllThingsStatus(s -> {
			if (org.springframework.util.StringUtils.isEmpty(s.getStatus())) {
				return;
			}

			ThingStatusInRule info = new ThingStatusInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(s.getStatus());

			general.updateThingStatus(s.getFullKiiThingID(),s.getStatus(),s.getModifyDate());
		});

		general.leaveInit();

		return errList;

	}

//
//	public void saveTrigger(TriggerRecord record){
//		triggerDao.addKiiEntity(record);
//	}

	public void createTrigger(TriggerRecord record) throws TriggerCreateException {


		Map<String,Set<String>>   map=getTriggerDataMap(record);

		general.addTriggerToEngine(record,map);

		return;

	}



	public Map<String,Set<String>> getTriggerDataMap(TriggerRecord record) throws TriggerCreateException {

		String triggerID=record.getId();

		try {

			Map<String,Set<String>> map=new HashMap<>();


			if (record instanceof SimpleTriggerRecord) {

				String thingID=addSimpleToEngine((SimpleTriggerRecord) record);

				map.put("comm",Collections.singleton(thingID));


			} else if (record instanceof GroupTriggerRecord) {
				GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
				Set<String> thingIDs=addGroupToEngine(groupRecord);

				map.put("comm",thingIDs);

				if (!groupRecord.getSource().getTagList().isEmpty()) {
					eventService.addGroupTagChangeListener(groupRecord.getSource().getTagList(), triggerID);
				}

			} else if (record instanceof SummaryTriggerRecord) {
				SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;

				map=addSummaryToEngine(summaryRecord);
				summaryRecord.getSummarySource().forEach((k, v) -> {
					eventService.addSummaryTagChangeListener(v.getSource().getTagList(), triggerID, k);
				});

			} else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;

				map=addMulToEngine(multipleRecord);

				multipleRecord.getSummarySource().forEach((k, v) -> {
					if(v instanceof GroupSummarySource) {
						eventService.addSummaryTagChangeListener(((GroupSummarySource)v).getSource().getTagList(), triggerID, k);
					}
				});

			}else{
				throw new InvalidTriggerFormatException("unsupport trigger type");

			}

			return map;

		} catch (TriggerCreateException e) {
//			triggerDao.deleteTriggerRecord(triggerID,e.getReason());
			throw e;
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

	private String  addSimpleToEngine(SimpleTriggerRecord record) {
		String thingID = null;
		if (record.getSource() != null) {
			GlobalThingInfo thingInfo = thingTagService.getThingByID(record.getSource().getThingID());
			if (thingInfo != null) {
				thingID = thingInfo.getFullKiiThingID();
			}else{

				throw EntryNotFoundException.thingNotFound(record.getSource().getThingID());
			}
		}

		return thingID;

	}

	private Map<String, Set<String>>  addSummaryToEngine(SummaryTriggerRecord record ){


		Map<String, Set<String>> summaryMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			if (v.getExpressList().stream().filter((exp) -> exp.getSlideFuntion() != null).findAny().isPresent() && !isStream.get()) {
				isStream.set(true);
			}
			;

			summaryMap.put(k, thingTagService.getKiiThingIDs(v.getSource()));
		});

		return summaryMap;

	}

	private  Set<String> addGroupToEngine(GroupTriggerRecord record){

		Set<String> thingIDs = thingTagService.getKiiThingIDs(record.getSource());

		return thingIDs;
	}




	private Map<String, Set<String>> addMulToEngine(MultipleSrcTriggerRecord record) {
		Map<String, Set<String>> thingMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			switch(v.getType()){
				case thing:
					ThingSource thing=(ThingSource)v;
					thingMap.put(k, Collections.singleton(thingTagService.getThingByID(Integer.parseInt(thing.getThingID())).getFullKiiThingID()));
				case summary:
					GroupSummarySource summary=(GroupSummarySource)v;
					thingMap.put(k, thingTagService.getKiiThingIDs(summary.getSource()));
					break;
			}
		});

		return thingMap;


	}

	

}
