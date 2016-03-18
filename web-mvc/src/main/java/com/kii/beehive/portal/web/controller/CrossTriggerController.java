package com.kii.beehive.portal.web.controller;


import java.util.*;

import com.kii.beehive.business.helper.OpLogTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@RestController
@RequestMapping(path = "/triggers", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class CrossTriggerController {

	@Autowired
	private TriggerManager mang;

	@Autowired
	private OpLogTools logTool;

	/**
	 * onboarding should be already done on the things in the param
	 *
	 * @param record
	 */
	@RequestMapping(path="/createTrigger",method = { RequestMethod.POST })
	public Map<String, Object> createTrigger(@RequestBody TriggerRecord record){


		record.setUserID(AuthInfoStore.getUserID());

		String triggerID = null;

		switch(record.getType()){
			case Simple:
				triggerID = mang.createSimpleTrigger((SimpleTriggerRecord)record);
				break;
			case Group:
				triggerID = mang.createGroupTrigger((GroupTriggerRecord)record);
				break;
			case Summary:
				triggerID = mang.createSummaryTrigger((SummaryTriggerRecord)record);
				break;
		}

		Map<String, Object> result = new HashMap<>();
		result.put("triggerID", triggerID);

		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”create"+当前triggerID
		List<String> list=new LinkedList<>();
		list.add(AuthInfoStore.getUserID());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("create");
		list.add(triggerID);
		logTool.write(list);
		return result;
	}

	@RequestMapping(path="/{triggerID}",method = { RequestMethod.DELETE })
	public void deleteTrigger(@PathVariable("triggerID") String triggerID){

		TriggerRecord record=mang.getTriggerByID(triggerID);

		verify(record);

		mang.deleteTrigger(triggerID);

		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”delete"+当前triggerID
		List<String> list=new LinkedList<>();
		list.add(AuthInfoStore.getUserID());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("delete");
		list.add(triggerID);
		logTool.write(list);
	}

	private void verify(TriggerRecord record) {
//		if(!record.getUserID().equals(AuthInfoStore.getUserID())
//				||!AuthInfoStore.isAmin()){
//
//			throw new BeehiveUnAuthorizedException("only owner can operate trigger");
//		}
		return;
	}

	@RequestMapping(path="/{triggerID}/enable",method = { RequestMethod.PUT })
	public void enableTrigger(@PathVariable("triggerID") String triggerID){
		mang.enableTrigger(triggerID);

		TriggerRecord record=mang.getTriggerByID(triggerID);
		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”enable"+当前triggerID
		List<String> list=new LinkedList<>();
		list.add(AuthInfoStore.getUserID());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("enable");
		list.add(triggerID);
		logTool.write(list);
	}

	@RequestMapping(path="/{triggerID}/disable",method = { RequestMethod.PUT })
	public void disableTrigger(@PathVariable("triggerID") String triggerID){
		mang.disableTrigger(triggerID);

		TriggerRecord record=mang.getTriggerByID(triggerID);
		//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”disable"+当前triggerID
		List<String> list=new LinkedList<>();
		list.add(AuthInfoStore.getUserID());
		list.add("trigger");
		list.add(record.getType().name());
		list.add("disable");
		list.add(triggerID);
		logTool.write(list);
	}

	@RequestMapping(path="/{triggerID}",method={RequestMethod.GET})
	public TriggerRecord getTriggerById(@PathVariable("triggerID") String triggerID){

		return mang.getTriggerByID(triggerID);

	}
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<TriggerRecord> getTriggerListByCurrentUser(){

		String currentUserId = AuthInfoStore.getUserID();

		return mang.getTriggerListByUserId(currentUserId);
//		return null;

	}

}