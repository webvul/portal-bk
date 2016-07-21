package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummarySource;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class TriggerManager {


	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private BusinessEventListenerService eventService;

	@Autowired
	private EngineService service;

	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private EventListenerDao eventListenerDao;

	@Autowired
	private ScheduleService scheduleService;


	public void reinit(){

		service.clear();
		scheduleService.clearTrigger();

		init();
	}

	@PostConstruct
	public void init(){
		List<TriggerRecord> recordList = triggerDao.getAllTrigger();

		recordList.forEach(record -> {

			try {
				addTriggerToEngine(record);
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		scheduleService.startSchedule();

		List<ThingStatusInRule>  initThings=new ArrayList<>();

		thingTagService.iteratorAllThingsStatus(s -> {
			if (StringUtils.isEmpty(s.getStatus())) {
				return;
			}
			ThingStatus status=null;
			try {
				status=mapper.readValue(s.getStatus(), ThingStatus.class);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			ThingStatusInRule info=new ThingStatusInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(status.getFields());
//			info.setThingID(s.getFullKiiThingID());

			initThings.add(info);

		});

		service.initThingStatus(initThings);


	}



	public Map<String,Object> getRuleEngingDump() {

			Map<String, Object> map = service.dumpEngineRuntime();


			map.put("schedule", scheduleService.dump());

			return map;
	}


	public String createTrigger(TriggerRecord record) {

		record.setRecordStatus(TriggerRecord.StatusType.disable);

		triggerDao.addKiiEntity(record);

		addTriggerToEngine(record);

		return record.getId();

	}

	private void addTriggerToEngine(TriggerRecord record) {

		String triggerID=record.getId();

		TriggerValidPeriod period=record.getPreparedCondition();
		if(period!=null){
			//ctrl enable sign by schedule.
			record.setRecordStatus(TriggerRecord.StatusType.disable);
		}

		try {
			if (record instanceof SimpleTriggerRecord) {
				addSimpleToEngine((SimpleTriggerRecord) record);
			} else if (record instanceof GroupTriggerRecord) {
				GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
				addGroupToEngine(groupRecord);
				if (!groupRecord.getSource().getTagList().isEmpty()) {
					eventService.addGroupTagChangeListener(groupRecord.getSource().getTagList(), triggerID);
				}

			} else if (record instanceof SummaryTriggerRecord) {
				SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;

				addSummaryToEngine(summaryRecord);
				summaryRecord.getSummarySource().forEach((k, v) -> {
					eventService.addSummaryTagChangeListener(v.getSource().getTagList(), triggerID, k);
				});

			} else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;

				addMulToEngine(multipleRecord);

				multipleRecord.getSummarySource().forEach((k, v) -> {
					if(v instanceof GroupSummarySource) {
						eventService.addSummaryTagChangeListener(((GroupSummarySource)v).getSource().getTagList(), triggerID, k);
					}
				});

			}else{
				throw new InvalidTriggerFormatException("unsupport trigger type");

			}

			if(period!=null) {
				scheduleService.addManagerTask(triggerID, period);
			}
		} catch (RuntimeException e) {

			triggerDao.deleteTriggerRecord(triggerID);
			throw e;

		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new InvalidTriggerFormatException("schedule init fail");
		}

		return ;
	}


	private void addSimpleToEngine(SimpleTriggerRecord record) {
		String thingID = null;
		if (record.getSource() != null) {
			GlobalThingInfo thingInfo = thingTagService.getThingByID(record.getSource().getThingID());
			if (thingInfo != null) {
				thingID = thingInfo.getFullKiiThingID();
			}
		}
		service.createSimpleTrigger(thingID,record);

	}


	private void addGroupToEngine(GroupTriggerRecord record) {

		Set<String> thingIDs = thingTagService.getKiiThingIDs(record.getSource());
		service.createGroupTrigger(record,thingIDs);
	}

	private void addSummaryToEngine(SummaryTriggerRecord record) {
		Map<String, Set<String>> thingMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			if (v.getExpressList().stream().filter((exp) -> exp.getSlideFuntion() != null).findAny().isPresent() && !isStream.get()) {
				isStream.set(true);
			}
			;

			thingMap.put(k, thingTagService.getKiiThingIDs(v.getSource()));
		});

		if(isStream.get()) {
//			service.createStreamSummaryTrigger(record, thingMap);
		}else{
			service.createSummaryTrigger(record,thingMap);
		}
	}



	private void addMulToEngine(MultipleSrcTriggerRecord record) {
		Map<String, Set<String>> thingMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			switch(v.getType()){
				case thing:
					ThingSource  thing=(ThingSource)v;
					thingMap.put(k, Collections.singleton(thingTagService.getThingByID(Integer.parseInt(thing.getThingID())).getFullKiiThingID()));
				case summary:
					SummarySource summary=(SummarySource)v;
					thingMap.put(k, thingTagService.getKiiThingIDs(summary.getSource()));
					break;
			}
		});

		if(isStream.get()) {
//			service.createStreamSummaryTrigger(record, thingMap);
		}else{
			service.createMultipleSourceTrigger(record,thingMap);
		}
	}
	public void disableTrigger(String triggerID) {
		triggerDao.disableTrigger(triggerID);

		service.disableTrigger(triggerID);

	}


	public void enableTrigger(String triggerID) {
		triggerDao.enableTrigger(triggerID);

		service.enableTrigger(triggerID);
	}

	public List<TriggerRecord> getTriggerListByUserId(String userId) {
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByUserId(userId);

		return triggerList;
	}

	public List<TriggerRecord> getDeleteTriggerListByUserId(String userId) {
		List<TriggerRecord> triggerList = triggerDao.getDeleteTriggerListByUserId(userId);

		return triggerList;
	}

	public List<SimpleTriggerRecord> getTriggerListByUserIdAndThingId(String userId, String thingId) {
		List<SimpleTriggerRecord> resultTriggerList = new ArrayList<SimpleTriggerRecord>();
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByUserId(userId);
		for (TriggerRecord trigger : triggerList) {
			if (trigger instanceof SimpleTriggerRecord) {
				SimpleTriggerRecord simpleTriggerRecord = (SimpleTriggerRecord) trigger;

				if (simpleTriggerRecord.getSource() == null) {
					continue;
				}
				String currThingId = simpleTriggerRecord.getSource().getThingID() + "";
				if (thingId.equals(currThingId)) {
					resultTriggerList.add(simpleTriggerRecord);
				}
			}
		}

		return resultTriggerList;
	}

	public TriggerRecord getTriggerByID(String triggerID) {

		TriggerRecord record = triggerDao.getTriggerRecord(triggerID);
		if (record == null) {
			throw  EntryNotFoundException.tagNameNotFound(triggerID);
		}
		return record;
	}

	public void deleteTrigger(String triggerID) {

		triggerDao.deleteTriggerRecord(triggerID);

		service.removeTrigger(triggerID);

		scheduleService.removeManagerTaskForSchedule(triggerID);

		List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
		for (EventListener eventListener : eventListenerList) {
			eventListenerDao.removeEntity(eventListener.getId());
		}
	}


}
