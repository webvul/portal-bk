package com.kii.beehive.portal.web.controller;


import java.util.Collections;
import java.util.Date;
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

import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.business.ruleengine.TriggerValidate;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@RestController
@RequestMapping(path = "/triggers", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class CrossTriggerController {

	@Autowired
	private TriggerManager mang;

	@Autowired
	private TriggerValidate triggerValidate;
	
	@Autowired
	private TagThingManager  manager;


	/**
	 * onboarding should be already done on the things in the param
	 *
	 * @param record
	 */
	@RequestMapping(path = "/createTrigger", method = {RequestMethod.POST})
	public Map<String, Object> createTrigger(@RequestBody TriggerRecord record) {

		record.setUserID(AuthInfoStore.getUserID());

		triggerValidate.validateTrigger(record);


		record.setRecordStatus(TriggerRecord.StatusType.enable);

		record.setUsedByWho(TriggerRecord.UsedByType.User);
		
		TriggerRecord trigger = mang.createTrigger(record);

		Map<String, Object> result = new HashMap<>();
		result.put("triggerID", trigger.getTriggerID());
		result.put("triggerPosition", trigger.getType()== BeehiveTriggerType.Gateway?"local":"cloud");


		return result;
	}

	@RequestMapping(path = "/{triggerID}", method = {RequestMethod.PUT},consumes={MediaType.ALL_VALUE})
	public Map<String, Object> updateTrigger(@PathVariable("triggerID") String triggerID, @RequestBody TriggerRecord record) {
		Map<String, Object> result = new HashMap<>();
		result.put("target", "success");

		record.setId(triggerID);

		record.setUserID(AuthInfoStore.getUserID());

		triggerValidate.validateTrigger(record);

		mang.updateTrigger(record);

		return result;
	}

	@RequestMapping(path = "/{triggerID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public Map<String, Object> deleteTrigger(@PathVariable("triggerID") String triggerID) {
		Map<String, Object> result = new HashMap<>();
		result.put("target", "success");

		mang.deleteTrigger(triggerID);


		return result;
	}

	@RequestMapping(path = "/{triggerID}/enable", method = {RequestMethod.PUT},consumes={MediaType.ALL_VALUE})
	public Map<String, Object> enableTrigger(@PathVariable("triggerID") String triggerID) {
		Map<String, Object> result = new HashMap<>();
		result.put("target", "success");
		TriggerRecord record = mang.getTriggerByID(triggerID);
		if (!TriggerRecord.StatusType.disable.equals(record.getRecordStatus())) {
			throw new PortalException(ErrorCode.INVALID_INPUT,"field","enable","data","true");
		}

		mang.enableTrigger(triggerID);


		return result;
	}


	@RequestMapping(path = "/{triggerID}/disable", method = {RequestMethod.PUT},consumes={MediaType.ALL_VALUE})
	public Map<String, Object> disableTrigger(@PathVariable("triggerID") String triggerID) {
		Map<String, Object> result = new HashMap<>();
		result.put("target", "success");
		TriggerRecord record = mang.getTriggerByID(triggerID);
		if (!TriggerRecord.StatusType.enable.equals(record.getRecordStatus())) {
			throw new PortalException(ErrorCode.INVALID_INPUT,"field","enable","data","false");
		}

		mang.disableTrigger(triggerID);


		return result;
	}

	@RequestMapping(path = "/{triggerID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public TriggerRecord getTriggerById(@PathVariable("triggerID") String triggerID) {

		return mang.getTriggerByID(triggerID);

	}

	@RequestMapping(path = "/all", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getTriggerListByCurrentUser() {

		Long currentUserId = AuthInfoStore.getUserID();

		return mang.getTriggerListByUserId(currentUserId);
//		return null;

	}

	@RequestMapping(path = "/deleteTrigger", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getDeleteTriggerListByCurrentUser() {

		Long currentUserId = AuthInfoStore.getUserID();

		return mang.getDeleteTriggerListByUserId(currentUserId);
//		return null;

	}

	@RequestMapping(path = "/things/{thingId}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getTriggerListByThingIdAndUserId(@PathVariable("thingId") long thingId) {
		
		return mang.getTriggerListByUserIdAndThingId(thingId);
	}


	@RequestMapping(path="/gateway",method={RequestMethod.GET},consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getGatewayTriggerList(){

		return mang.getAllGatewayTrigger();

	}

	@RequestMapping(path="/gateway/{vendorThingID}",method={RequestMethod.GET},consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getTriggerListByGatewayVendorThingID(@PathVariable("vendorThingID") String vendorThingID){

		return mang.getTriggerListByGatewayVendorThingID(vendorThingID);

	}




	@RequestMapping(path = "/debug/dump", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public Map<String, Object> getRuleEngineDump() {

		return mang.getRuleEngingDump();
	}

	@RequestMapping(path = "/debug/dump/{triggerID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public Map<String, Object> getRuleEngineDumpByID(@PathVariable("triggerID") String triggerID) {

		return mang.getRuleEngingDump(triggerID);
	}
	
	
	@Autowired
	private BeehiveTriggerService engine;
	
	@RequestMapping(path="/debug/pushThStatus/{thingID}",method={RequestMethod.PUT})
	public  void  setThingStatus(@PathVariable("thingID") Long thingID, @RequestBody ThingStatus status){
		
		
		GlobalThingInfo info=manager.getThingsByIds(Collections.singletonList(thingID)).get(0);
		
		BusinessDataObject obj=new BusinessDataObject(String.valueOf(info.getId()),null, BusinessObjType.Thing);
		obj.setData(status.getFields());
		obj.setCreated(new Date());
		
		engine.updateBusinessData(obj);
		
	}


}
