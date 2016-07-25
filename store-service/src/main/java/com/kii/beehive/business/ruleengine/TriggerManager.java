package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.helper.TriggerCreator;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class TriggerManager {

	private static final Logger log= LoggerFactory.getLogger(TriggerManager.class);

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

	@Autowired
	private TriggerCreator  creator;


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
				creator.addTriggerToEngine(record);
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
				log.error("invalid thing "+s.getId()+" status ",e);
				return;
			}
			ThingStatusInRule info=new ThingStatusInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(status.getFields());

			initThings.add(info);

		});

		service.initThingStatus(initThings);


	}


	public String createTrigger(TriggerRecord record) {

		triggerDao.addKiiEntity(record);

		creator.addTriggerToEngine(record);

		return record.getId();

	}


	public Map<String,Object> getRuleEngingDump() {

			Map<String, Object> map = service.dumpEngineRuntime();


			map.put("schedule", scheduleService.dump());

			return map;
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
