package com.kii.beehive.business.helper;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;

@Component
public class TriggerCreator {

	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private BusinessEventListenerService eventService;



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private ScheduleService scheduleService;



	@Autowired
	private EngineService service;


	public String createTrigger(TriggerRecord record) {

		triggerDao.addKiiEntity(record);

		addTriggerToEngine(record);

		return record.getId();

	}

	public void addTriggerToEngine(TriggerRecord record) {

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

			e.printStackTrace();
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
			}else{

				throw EntryNotFoundException.thingNotFound(record.getSource().getThingID());
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
					ThingSource thing=(ThingSource)v;
					thingMap.put(k, Collections.singleton(thingTagService.getThingByID(Integer.parseInt(thing.getThingID())).getFullKiiThingID()));
				case summary:
					GroupSummarySource summary=(GroupSummarySource)v;
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


}
