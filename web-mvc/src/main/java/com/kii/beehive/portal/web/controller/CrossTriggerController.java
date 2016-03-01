//package com.kii.beehive.portal.web.controller;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kii.beehive.business.ruleengine.TriggerManager;
//import com.kii.beehive.portal.auth.AuthInfoStore;
//import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
//import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
//import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
//import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
//import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
//
//@RestController
//@RequestMapping(path = "/triggers", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
//		MediaType.APPLICATION_JSON_UTF8_VALUE })
//public class CrossTriggerController {
//
//
//	@Autowired
//	private TriggerManager manager;
//
//
//
//	/**
//	 * onboarding should be already done on the things in the param
//	 *
//	 * @param record
//     */
//	@RequestMapping(path="/createTrigger",method = { RequestMethod.POST })
//	public Map<String, Object> createTrigger(@RequestBody TriggerRecord record){
//
//
//		record.setUserID(AuthInfoStore.getUserID());
//
//		String triggerID = null;
//
//		switch(record.getType()){
//			case Simple:
//				triggerID = manager.createSimpleTrigger((SimpleTriggerRecord)record);
//				break;
//			case Group:
//				triggerID = manager.createGroupTrigger((GroupTriggerRecord)record);
//				break;
//			case Summary:
//				triggerID = manager.createSummaryTrigger((SummaryTriggerRecord)record);
//				break;
//		}
//
//		Map<String, Object> result = new HashMap<>();
//		result.put("triggerID", triggerID);
//
//		return result;
//	}
//
//	@RequestMapping(path="/{triggerID}",method = { RequestMethod.DELETE })
//	public void deleteTrigger(@PathVariable("triggerID") String triggerID){
//
//		TriggerRecord record=mang.getTriggerRecord(triggerID);
//
//		verify(record);
//
//
//		switch(record.getType()){
//			case Simple:
//				simpleMang.removeSimpleTrigger(triggerID);
//				break;
//			case Group:
//				groupMang.removeTrigger(triggerID);
//				break;
//			case Summary:
//				summaryMang.removeTrigger(triggerID);
//				break;
//		}
//
//	}
//
//	private void verify(TriggerRecord record) {
//		if(!record.getUserID().equals(AuthInfoStore.getUserID())
//				&&!AuthInfoStore.isAmin()){
//
//			throw new BeehiveUnAuthorizedException("only owner can operate trigger");
//		}
//	}
//
//	@RequestMapping(path="/{triggerID}/enable",method = { RequestMethod.PUT })
//	public void enableTrigger(@PathVariable("triggerID") String triggerID){
//
//
//		mang.enableTrigger(triggerID);
//
//	}
//
//	@RequestMapping(path="/{triggerID}/disable",method = { RequestMethod.PUT })
//	public void disableTrigger(@PathVariable("triggerID") String triggerID){
//
//		mang.disableTrigger(triggerID);
//	}
//
//	@RequestMapping(path="/{triggerID}",method={RequestMethod.GET})
//	public TriggerRecord getTriggerList(@PathVariable("triggerID") String triggerID){
//
//		return mang.getTriggerRecord(triggerID);
//
//	}
//
//}
