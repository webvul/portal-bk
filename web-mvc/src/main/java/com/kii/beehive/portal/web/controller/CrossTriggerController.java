package com.kii.beehive.portal.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.manager.TriggerMaintainManager;
import com.kii.beehive.portal.manager.SimpleThingTriggerManager;
import com.kii.beehive.portal.manager.ThingGroupStateManager;
import com.kii.beehive.portal.manager.ThingStateSummaryManager;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;

@RestController
@RequestMapping(path = "/triggers", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class CrossTriggerController {


	@Autowired
	private ThingGroupStateManager groupMang;

	@Autowired
	private SimpleThingTriggerManager  simpleMang;

	@Autowired
	private ThingStateSummaryManager  summaryMang;

	private TriggerMaintainManager mang;


	@RequestMapping(path="/createTrigger",method = { RequestMethod.POST })
	public void createTrigger(@RequestBody TriggerRecord record){


		record.setUserID(AuthInfoStore.getUserID());

		switch(record.getType()){
			case Simple:
				simpleMang.createSimpleTrigger((SimpleTriggerRecord)record);
				break;
			case Group:
				groupMang.createThingGroup((GroupTriggerRecord)record);
				break;
			case Summary:
				summaryMang.initStateSummary((SummaryTriggerRecord)record);
				break;
		}

	}

	@RequestMapping(path="/{triggerID}",method = { RequestMethod.DELETE })
	public void deleteTrigger(@PathVariable("triggerID") String triggerID){

		TriggerRecord record=mang.getTriggerRecord(triggerID);

		verify(record);


		switch(record.getType()){
			case Simple:
				simpleMang.removeSimpleTrigger(triggerID);
				break;
			case Group:
				groupMang.removeTrigger(triggerID);
				break;
			case Summary:
				summaryMang.removeTrigger(triggerID);
				break;
		}

	}

	private void verify(TriggerRecord record) {
		if(!record.getUserID().equals(AuthInfoStore.getUserID())
				&&!AuthInfoStore.isAmin()){

			throw new BeehiveUnAuthorizedException("only owner can operate trigger");
		}
	}

	@RequestMapping(path="/{triggerID}/enable",method = { RequestMethod.PUT })
	public void enableTrigger(@PathVariable("triggerID") String triggerID){


		mang.enableTrigger(triggerID);

	}

	@RequestMapping(path="/{triggerID}/disable",method = { RequestMethod.PUT })
	public void disableTrigger(@PathVariable("triggerID") String triggerID){

		mang.disableTrigger(triggerID);
	}


}
