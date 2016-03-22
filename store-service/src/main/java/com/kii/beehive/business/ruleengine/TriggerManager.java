package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
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
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
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


		List<TriggerRecord> recordList=triggerDao.getAllEnableTrigger();

		recordList.forEach(record->{

			if(record instanceof SimpleTriggerRecord){
				addSimpleToEngine((SimpleTriggerRecord)record);
			}else if(record instanceof GroupTriggerRecord){
				GroupTriggerRecord groupRecord=((GroupTriggerRecord)record);
				addGroupToEngine(groupRecord);

			}else if(record instanceof  SummaryTriggerRecord){
				SummaryTriggerRecord  summaryRecord=(SummaryTriggerRecord)record;
				addSummaryToEngine(summaryRecord);

			}else{
				throw new IllegalArgumentException("unsupport trigger type");
			}

		});

		thingTagService.iteratorAllThingsStatus( s->{

			if(StringUtils.isEmpty(s.getStatus())){
				return;
			}
			try {
				ThingStatus status = mapper.readValue(s.getStatus(),ThingStatus.class);
				String id=s.getFullKiiThingID();

				service.updateThingStatus(id,status,s.getModifyDate());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});


	}

	public String createTrigger(TriggerRecord record){

//		record.setRecordStatus(TriggerRecord.StatusType.enable);
		record.setRecordStatus(TriggerRecord.StatusType.disable);

		String triggerID=triggerDao.addEntity(record).getObjectID();

		record.setId(triggerID);

		if(record instanceof SimpleTriggerRecord){
			addSimpleToEngine((SimpleTriggerRecord)record);
		}else if(record instanceof GroupTriggerRecord){
			GroupTriggerRecord groupRecord=((GroupTriggerRecord)record);
			if(!groupRecord.getSource().getSelector().getTagList().isEmpty()) {

				eventService.addGroupTagChangeListener(groupRecord.getSource().getSelector().getTagList(), triggerID);
			}
			addGroupToEngine(groupRecord);

		}else if(record instanceof  SummaryTriggerRecord){
			SummaryTriggerRecord  summaryRecord=(SummaryTriggerRecord)record;

			summaryRecord.getSummarySource().forEach((k,v)->{
				eventService.addSummaryTagChangeListener(v.getSource().getSelector().getTagList(),triggerID,k);
			});

			addSummaryToEngine(summaryRecord);

		}else{
			throw new IllegalArgumentException("unsupport trigger type");
		}

		eventService.addBeehiveTriggerChangeListener(triggerID);

		return triggerID;

	}



	private void addSimpleToEngine(SimpleTriggerRecord record) {
		String triggerID=record.getId();
		String thingID=null;
		if(record.getSource()!=null) {
			GlobalThingInfo thingInfo = thingTagService.getThingByID(record.getSource().getThingID());
			if(thingInfo != null) {
				thingID = thingInfo.getFullKiiThingID();
			}
		}
		service.createSimpleTrigger(thingID,triggerID,record.getPredicate());
	}


	private void addGroupToEngine(GroupTriggerRecord record) {

		Set<String> thingIDs;
		if(!record.getSource().getSelector().getThingList().isEmpty()) {

			thingIDs = thingTagService.getTagNamesByIDs(record.getSource().getSelector().getThingList());
		}else {
			thingIDs = thingTagService.getKiiThingIDs(record.getSource().getSelector());
		}
		service.createGroupTrigger(thingIDs,record.getPolicy(),record.getId(),record.getPredicate());
	}

	private void addSummaryToEngine(SummaryTriggerRecord record) {
		Map<String,Set<String>> thingMap=new HashMap<>();

		final AtomicBoolean isStream=new AtomicBoolean(false);

		record.getSummarySource().forEach((k,v)->{

			if(v.getExpressList().stream().filter((exp)->exp.getSlideFuntion()!=null).findAny().isPresent() && !isStream.get()){
				isStream.set(true);
			};

			thingMap.put(k,thingTagService.getKiiThingIDs(v.getSource().getSelector()));
		});

		service.createSummaryTrigger(record,thingMap,isStream.get());
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

	public List<TriggerRecord> getTriggerListByUserId(String userId){
		List<TriggerRecord> triggerList= triggerDao.getTriggerListByUserId(userId);

		return triggerList;
	}

	public List<TriggerRecord> getDeleteTriggerListByUserId(String userId){
		List<TriggerRecord> triggerList= triggerDao.getDeleteTriggerListByUserId(userId);

		return triggerList;
	}

	public List<SimpleTriggerRecord> getTriggerListByUserIdAndThingId(String userId,String thingId){
		List<SimpleTriggerRecord> resultTriggerList = new ArrayList<SimpleTriggerRecord>();
		List<TriggerRecord> triggerList= triggerDao.getTriggerListByUserId(userId);
		for(TriggerRecord trigger : triggerList){
			if(trigger instanceof SimpleTriggerRecord){
				SimpleTriggerRecord simpleTriggerRecord = (SimpleTriggerRecord)trigger;

				if(simpleTriggerRecord.getSource()==null ){
					continue;
				}
				String currThingId = simpleTriggerRecord.getSource().getThingID()+"";
				if(thingId.equals(currThingId)){
					resultTriggerList.add(simpleTriggerRecord);
				}
			}
		}

		return resultTriggerList;
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
