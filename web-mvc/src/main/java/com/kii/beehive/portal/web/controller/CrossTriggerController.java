package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;

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
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.web.constant.CallbackNames;

@RestController
@RequestMapping(path = "/triggers", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class CrossTriggerController {

	@Autowired
	private TriggerManager mang;

	
	@Autowired
	private TagThingManager  manager;
	
	
	private String getRemoteUrl(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String subUrl = "";
		if (url.contains("/triggers")) {
			subUrl = url.substring(0, url.indexOf("/triggers"))+ CallbackNames.CALLBACK_URL ;
		}
		return subUrl;
	}

	@RequestMapping(path = "/createTrigger", method = {RequestMethod.POST})
	public Map<String, Object> createTrigger(@RequestBody TriggerRecord record) {

		record.setUserID(AuthInfoStore.getUserID());
		
		record.setRecordStatus(TriggerRecord.StatusType.enable);

		record.setUsedByWho(TriggerRecord.UsedByType.User);
		
		TriggerRecord trigger = mang.createTrigger(record);

		Map<String, Object> result = new HashMap<>();
		result.put("triggerID", trigger.getTriggerID());
		result.put("triggerPosition", trigger.getType()== BeehiveTriggerType.Gateway?"local":"cloud");

		return result;
	}

	@RequestMapping(path = "/{triggerID}", method = {RequestMethod.PUT})
	public void  updateTrigger(@PathVariable("triggerID") String triggerID, @RequestBody TriggerRecord record) {
		
		record.setId(triggerID);

		record.setUserID(AuthInfoStore.getUserID());
		
		mang.updateTrigger(record);

		return;
	}

	@RequestMapping(path = "/{triggerID}", method = {RequestMethod.DELETE}, consumes = {MediaType.ALL_VALUE})
	public void deleteTrigger(@PathVariable("triggerID") String triggerID) {

		mang.deleteTrigger(triggerID);


		return ;
	}

	@RequestMapping(path = "/{triggerID}/enable", method = {RequestMethod.PUT},consumes={MediaType.ALL_VALUE})
	public void enableTrigger(@PathVariable("triggerID") String triggerID) {

		mang.enableTrigger(triggerID);


		return ;
	}


	@RequestMapping(path = "/{triggerID}/disable", method = {RequestMethod.PUT},consumes={MediaType.ALL_VALUE})
	public void disableTrigger(@PathVariable("triggerID") String triggerID) {
	

		mang.disableTrigger(triggerID);


		return ;
	}

	@RequestMapping(path = "/{triggerID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public TriggerRecord getTriggerById(@PathVariable("triggerID") String triggerID) {

		return mang.getTriggerByID(triggerID);

	}

	@RequestMapping(path = "/all", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getTriggerListByCurrentUser() {

		Long currentUserId = AuthInfoStore.getUserID();

		return mang.getTriggerListByUserId(currentUserId);

	}

	@RequestMapping(path = "/deleteTrigger", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public List<TriggerRecord> getDeleteTriggerListByCurrentUser() {

		Long currentUserId = AuthInfoStore.getUserID();

		return mang.getDeleteTriggerListByUserId(currentUserId);

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




}
