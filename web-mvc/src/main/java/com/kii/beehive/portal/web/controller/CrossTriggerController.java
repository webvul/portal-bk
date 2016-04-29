package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.business.ruleengine.TriggerLogTools;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.web.exception.MethodNotAllowedException;
import com.kii.extension.ruleengine.TriggerValidate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@RestController
@RequestMapping(path = "/triggers", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class CrossTriggerController {

	@Autowired
	private TriggerManager mang;

	@Autowired
	private OpLogTools logTool;

	@Autowired
	private TriggerValidate triggerValidate;

	@Autowired
	private TriggerLogTools triggerLogTools;

	/**
	 * onboarding should be already done on the things in the param
	 *
	 * @param record
	 */
	@RequestMapping(path="/createTrigger",method = { RequestMethod.POST })
	public Map<String, Object> createTrigger(@RequestBody TriggerRecord record){

		record.setUserID(AuthInfoStore.getUserID());

		triggerValidate.validateTrigger(record);

		String triggerID = null;

		triggerID=mang.createTrigger(record);

		Map<String, Object> result = new HashMap<>();
		result.put("triggerID", triggerID);

		triggerLogTools.outputCreateLog(record,triggerID);

		return result;
	}

	@RequestMapping(path="/{triggerID}",method = { RequestMethod.DELETE },consumes = {"*"})
	public Map<String, Object> deleteTrigger(@PathVariable("triggerID") String triggerID){
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");

		mang.deleteTrigger(triggerID);

		triggerLogTools.outputDeleteLog(triggerID);

		return result;
	}



	@RequestMapping(path="/{triggerID}/enable",method = { RequestMethod.PUT })
	public Map<String, Object> enableTrigger(@PathVariable("triggerID") String triggerID){
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		TriggerRecord record=mang.getTriggerByID(triggerID);
		if(!TriggerRecord.StatusType.disable.equals(record.getRecordStatus())){
			throw new MethodNotAllowedException("only can operating disable Trigger");
		}

		mang.enableTrigger(triggerID);

		triggerLogTools.outputEnableLog(record);

		return result;
	}


	@RequestMapping(path="/{triggerID}/disable",method = { RequestMethod.PUT })
	public Map<String, Object> disableTrigger(@PathVariable("triggerID") String triggerID){
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		TriggerRecord record=mang.getTriggerByID(triggerID);
		if(!TriggerRecord.StatusType.enable.equals(record.getRecordStatus())){
			throw new MethodNotAllowedException("only can operating enable Trigger");
		}

		mang.disableTrigger(triggerID);

		triggerLogTools.outputDisableLog(record);

		return result;
	}

	@RequestMapping(path="/{triggerID}",method={RequestMethod.GET},consumes = {"*"})
	public TriggerRecord getTriggerById(@PathVariable("triggerID") String triggerID){

		return mang.getTriggerByID(triggerID);

	}
	@RequestMapping(path="/all",method={RequestMethod.GET},consumes = {"*"})
	public List<TriggerRecord> getTriggerListByCurrentUser(){

		String currentUserId = AuthInfoStore.getUserID();

		return mang.getTriggerListByUserId(currentUserId);
//		return null;

	}

	@RequestMapping(path="/deleteTrigger",method={RequestMethod.GET},consumes = {"*"})
	public List<TriggerRecord> getDeleteTriggerListByCurrentUser(){

		String currentUserId = AuthInfoStore.getUserID();

		return mang.getDeleteTriggerListByUserId(currentUserId);
//		return null;

	}

	@RequestMapping(path="/things/{thingId}",method={RequestMethod.GET},consumes = {"*"})
	public List<SimpleTriggerRecord> getTriggerListByThingIdAndUserId(@PathVariable("thingId") String thingId){
		String currentUserId = AuthInfoStore.getUserID();
		return mang.getTriggerListByUserIdAndThingId(currentUserId,thingId);
	}


	@RequestMapping(path="/debug/dump",method={RequestMethod.GET},consumes = {"*"})
	public Map<String,Object> getRuleEngineDump(){

		return mang.getRuleEngingDump();
	}

	@RequestMapping(path="/debug/reinit",method={RequestMethod.POST},consumes = {"*"})
	public void reInit(){

		 mang.reinit();
	}
}
