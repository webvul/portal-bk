package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class TriggerManager {



	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private BusinessEventListenerService  eventService;

	@Autowired
	private EngineService  service;

	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private TriggerFireCallback callback;

	@Autowired
	private ObjectMapper mapper;



	@PostConstruct
	public void init(){


		List<TriggerRecord> recordList=triggerDao.getAllTrigger();

		recordList.forEach(t->createTrigger(t));

		thingTagService.iteratorAllThingsStatus( s->{

			if(StringUtils.isEmpty(s.getStatus())){
				return;
			}
			try {
				ThingStatus status = mapper.readValue(s.getStatus(),ThingStatus.class);
				String id=s.getFullKiiThingID();

				service.updateThingStatus(id,status);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});


	}

	public String createTrigger(TriggerRecord record){

		record.setRecordStatus(TriggerRecord.StatusType.enable);

		if(record instanceof SimpleTriggerRecord){
			return createSimpleTrigger((SimpleTriggerRecord)record);
		}else if(record instanceof GroupTriggerRecord){
			return createGroupTrigger((GroupTriggerRecord)record);
		}else if(record instanceof  SummaryTriggerRecord){
			return createSummaryTrigger((SummaryTriggerRecord)record);
		}else{
			throw new IllegalArgumentException("unsupport trigger type");
		}

	}


//
	public String createSimpleTrigger(SimpleTriggerRecord record){



		String triggerID=triggerDao.addEntity(record).getObjectID();

		String thingID=null;
		if(record.getSource()!=null) {
			thingID = thingTagService.getThingByID(record.getSource().getThingID()).getFullKiiThingID();
		}
		service.createSimpleTrigger(thingID,triggerID,record.getPredicate());

		eventService.addBeehiveTriggerChangeListener(triggerID);

		return triggerID;

	}

	public String createGroupTrigger(GroupTriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();


		if(!record.getSource().getSelector().getThingList().isEmpty()){

			Set<String> thingIDs=thingTagService.getTagNamesByIDs(record.getSource().getSelector().getThingList());

			service.createGroupTrigger(thingIDs,record.getPolicy(),triggerID,record.getPredicate());

			eventService.addBeehiveTriggerChangeListener(triggerID);
			return triggerID;

		}


		Set<String> thingIDs=thingTagService.getKiiThingIDs(record.getSource().getSelector());

		service.createGroupTrigger(thingIDs,record.getPolicy(),triggerID,record.getPredicate());

		eventService.addGroupTagChangeListener(record.getSource().getSelector().getTagList(),triggerID);

		eventService.addBeehiveTriggerChangeListener(triggerID);
		return triggerID;



	}

	public String createSummaryTrigger(SummaryTriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();

		Map<String,Set<String>> thingMap=new HashMap<>();

		final AtomicBoolean isStream=new AtomicBoolean(false);

		record.getSummarySource().forEach((k,v)->{

			if(v.getExpressList().stream().filter((exp)->exp.getSlideFuntion()!=null).findAny().isPresent() && !isStream.get()){
				isStream.set(true);
			};

			thingMap.put(k,thingTagService.getKiiThingIDs(v.getSource().getSelector()));
		});

		service.createSummaryTrigger(record,thingMap,isStream.get());

		record.getSummarySource().forEach((k,v)->{
			eventService.addSummaryTagChangeListener(v.getSource().getSelector().getTagList(),triggerID,k);
		});

		eventService.addBeehiveTriggerChangeListener(triggerID);

		return triggerID;

	}

	public void disableTrigger(String triggerID){
		triggerDao.disableTrigger(triggerID);
		service.disableTrigger(triggerID);
		eventService.disableTriggerByTargetID(triggerID);
	}


	public void enableTrigger(String triggerID){
		triggerDao.enableTrigger(triggerID);
		service.enableTrigger(triggerID);
		eventService.enableTriggerByTargetID(triggerID);
	}

	public TriggerRecord  getTriggerByID(String triggerID){

		TriggerRecord record= triggerDao.getTriggerRecord(triggerID);
		if(record==null){
			throw new EntryNotFoundException(triggerID);
		}
		return record;
	}
	
	public void deleteTrigger(String triggerID) {

		triggerDao.deleteTriggerRecord(triggerID);
	}


}
