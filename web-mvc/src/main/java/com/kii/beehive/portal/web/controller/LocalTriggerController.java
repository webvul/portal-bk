package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.business.ruleengine.LocalTriggerManager;
import com.kii.beehive.business.ruleengine.TriggerLogTools;
import com.kii.beehive.business.ruleengine.TriggerValidate;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@RestController
@RequestMapping(path = "/local/triggers", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class LocalTriggerController {

	@Autowired
	private LocalTriggerManager localTriggerManager;

	@Autowired
	private OpLogTools logTool;

	@Autowired
	private TriggerValidate triggerValidate;

	@Autowired
	private TriggerLogTools triggerLogTools;

//	/**
//	 * onboarding should be already done on the things in the param
//	 *
//	 * @param record
//	 */
//	@RequestMapping(path="/createTrigger",method = { RequestMethod.POST })
//	public Map<String, Object> createTrigger(@RequestBody TriggerRecord record){
//
//		record.setUserID(AuthInfoStore.getUserID());
//
//		triggerValidate.validateTrigger(record);
//
//		String triggerID = null;
//
//		record.setRecordStatus(TriggerRecord.StatusType.disable);
//
//		triggerID=localTriggerManager.createTrigger(record);
//
//		Map<String, Object> result = new HashMap<>();
//		result.put("triggerID", triggerID);
//		result.put("triggerPosition", record.getTriggerPosition());
//
//		triggerLogTools.outputCreateLog(record,triggerID);
//
//		return result;
//	}


	@RequestMapping(path="/{triggerID}/enable",method = { RequestMethod.PUT })
	public Map<String, Object> enableTrigger(@PathVariable("triggerID") String triggerID){
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		TriggerRecord record= localTriggerManager.getTriggerByID(triggerID);
		if(!TriggerRecord.StatusType.disable.equals(record.getRecordStatus())){
			throw new PortalException(ErrorCode.INVALID_INPUT);
		}

		localTriggerManager.enableTrigger(triggerID);

//		triggerLogTools.outputEnableLog(record);

		return result;
	}


	@RequestMapping(path="/{triggerID}/disable",method = { RequestMethod.PUT })
	public Map<String, Object> disableTrigger(@PathVariable("triggerID") String triggerID){
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		TriggerRecord record= localTriggerManager.getTriggerByID(triggerID);
		if(!TriggerRecord.StatusType.enable.equals(record.getRecordStatus())){
			throw new PortalException(ErrorCode.INVALID_INPUT);
		}

		localTriggerManager.disableTrigger(triggerID);

//		triggerLogTools.outputDisableLog(record);

		return result;
	}


	@RequestMapping(path="/{triggerID}",method = { RequestMethod.DELETE },consumes = {"*"})
	public Map<String, Object> deleteTrigger(@PathVariable("triggerID") String triggerID){
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");

		localTriggerManager.deleteTrigger(triggerID);

//		triggerLogTools.outputDeleteLog(triggerID);

		return result;
	}



	@RequestMapping(path="/{triggerID}",method={RequestMethod.GET},consumes = {"*"})
	public TriggerRecord getTriggerById(@PathVariable("triggerID") String triggerID){

		return localTriggerManager.getTriggerByID(triggerID);

	}

	@RequestMapping(path="/gateway/{vendorThingID}",method={RequestMethod.GET},consumes = {"*"})
	public List<TriggerRecord> getTriggerListByGatewayVendorThingID(@PathVariable("vendorThingID") String vendorThingID){

		return localTriggerManager.getTriggerListByGatewayVendorThingID(vendorThingID);

	}


	@RequestMapping(path="/all",method={RequestMethod.GET},consumes = {"*"})
	public List<TriggerRecord> getAllTrigger(){

		return localTriggerManager.getAllTrigger();
	}

	@RequestMapping(path="/deleteTrigger",method={RequestMethod.GET},consumes = {"*"})
	public List<TriggerRecord> getDeleteTriggerListByCurrentUser(){

		Long currentUserId = AuthInfoStore.getUserID();

		return localTriggerManager.getDeleteTriggerListByUserId(currentUserId);

	}



}
