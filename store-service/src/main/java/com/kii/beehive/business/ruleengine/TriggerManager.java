package com.kii.beehive.business.ruleengine;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingStateManager;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.extension.ruleengine.EngineService;

@Component
public class TriggerManager {



	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private BusinessEventListenerService  eventService;

	@Autowired
	private EngineService  service;

	@Autowired
	private ThingStateManager thingTagService;


//
	public String createSimpleTrigger(SimpleTriggerRecord record){



		String triggerID=triggerDao.addEntity(record).getObjectID();


		String thingID=thingTagService.getThingByID(record.getSource().getThingID()).getFullKiiThingID();

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

		service.createSummaryTrigger(record);

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

		return triggerDao.getTriggerRecord(triggerID);
	}
//	public void deleteTrigger(String triggerID){
//
//		triggerDao.deleteTriggerRecord(triggerID);
//
//		service.removeTrigger(triggerID);
//
//		eventService.removeListener();
//
//	}

}
