//package com.kii.beehive.business.ruleengine;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.business.manager.AppInfoManager;
//import com.kii.beehive.business.manager.ThingTagManager;
//import com.kii.beehive.business.service.ThingIFInAppService;
//import com.kii.beehive.portal.common.utils.ThingIDTools;
//import com.kii.beehive.portal.exception.EntryNotFoundException;
//import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
//import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
//import com.kii.extension.ruleengine.service.TriggerRecordDao;
//import com.kii.extension.ruleengine.store.trigger.CommandToThing;
//import com.kii.extension.ruleengine.store.trigger.CommandToThingInGW;
//import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
//import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
//import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
//import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
//import com.kii.extension.ruleengine.store.trigger.WhenType;
//import com.kii.extension.ruleengine.store.trigger.condition.LogicCol;
//import com.kii.extension.sdk.entity.thingif.Action;
//import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
//import com.kii.extension.sdk.entity.thingif.ThingCommand;
//import com.kii.extension.sdk.entity.thingif.ThingOfKiiCloud;
//import com.kii.extension.sdk.exception.ObjectNotFoundException;
//
//@Component
//public class LocalTriggerManager {
//
//	private static final Logger log= LoggerFactory.getLogger(LocalTriggerManager.class);
//
//	@Autowired
//	private ThingIFInAppService thingIFService;
//
//	@Autowired
//	private AppInfoManager appInfoManager;
//
//	@Autowired
//	private TriggerRecordDao triggerRecordDao;
//
//	@Autowired
//	private GlobalThingSpringDao globalThingDao;
//
//	@Autowired
//	private ThingTagManager thingTagService;
//
//
//
//
//
//	public boolean checkLocalRule(TriggerRecord record) {
//
//		boolean isLocalRule = false;
//		if( ! (record instanceof GroupTriggerRecord
//				&& record.getPredicate().getTriggersWhen().equals(WhenType.CONDITION_TRUE)
//				&& record.getPredicate().getSchedule() == null
//				&& ! ( record.getPredicate().getCondition()  instanceof LogicCol ) ) ) {
//
//			return false;
//		}
//
//			GroupTriggerRecord  groupRecord=(GroupTriggerRecord)record;
//
//			//source only one thing
//			if( ( groupRecord.getSource().getTagList() != null && groupRecord.getSource().getTagList().size() > 0 )
//					||  ( groupRecord.getSource().getThingList() != null && groupRecord.getSource().getThingList().size() > 1 )){
//				return false;
//			}
//
//		return true ;
//	}
//
//
//
//
//
//	public void enableTrigger(String triggerID) {
//		TriggerRecord triggerRecord = triggerRecordDao.getObjectByID(triggerID);
//
//		if(triggerRecord ==null ) {
//			ObjectNotFoundException e= new ObjectNotFoundException();
//			e.setBucketID("localTriggerRecord");
//			e.setObjectID(triggerID);
//			throw e;
//		}
//		triggerRecordDao.enableTrigger(triggerID);
//
//		//action
//		Map<String, Action> actions = new HashMap<>();
//		Action action = new Action();
//		actions.put("enableTrigger", action);
//		action.setField("triggerID", triggerID);
//
//		sendGatewayCommand(triggerRecord.getGatewayFullKiiThingID(), actions);
//	}
//
//
//	public void disableTrigger(String triggerID) {
//
//		GatewayTriggerRecord triggerRecord = localTriggerRecordDao.getObjectByID(triggerID);
//
//		if(triggerRecord ==null ) {
//			ObjectNotFoundException e= new ObjectNotFoundException();
//			e.setBucketID("localTriggerRecord");
//			e.setObjectID(triggerID);
//			throw e;
//		}
//		localTriggerRecordDao.disableTrigger(triggerID);
//
//		//action
//		Map<String, Action> actions = new HashMap<>();
//		Action action = new Action();
//		actions.put("disableTrigger", action);
//		action.setField("triggerID", triggerID);
//
//		sendGatewayCommand(triggerRecord.getGatewayFullKiiThingID(), actions);
//
//	}
//
//
//	public void deleteTrigger(String triggerID) {
//
//		GatewayTriggerRecord triggerRecord = localTriggerRecordDao.getObjectByID(triggerID);
//
//		if(triggerRecord ==null ) {
//			ObjectNotFoundException e= new ObjectNotFoundException();
//			e.setBucketID("localTriggerRecord");
//			e.setObjectID(triggerID);
//			throw e;
//		}
//		localTriggerRecordDao.deleteTriggerRecord(triggerID);
//
//
//	}
//
//
////	public GatewayTriggerRecord getTriggerByID(String triggerID) {
////
////		GatewayTriggerRecord record = localTriggerRecordDao.getTriggerRecord(triggerID);
////		if (record == null) {
////			throw  EntryNotFoundException.tagNameNotFound(triggerID);
////		}
////		return record;
////	}
//
////
////	public List<GatewayTriggerRecord> getAllTrigger() {
////		List<GatewayTriggerRecord> triggerList = localTriggerRecordDao.getAllTrigger();
////
////		return triggerList;
////	}
////	public List<GatewayTriggerRecord> getTriggerListByGatewayVendorThingID(String vendorThingID) {
////		List<GatewayTriggerRecord> triggerList = localTriggerRecordDao.getTriggerListByGatewayVendorThingID(vendorThingID);
////
////		return triggerList;
////	}
////
////
////	public List<GatewayTriggerRecord> getTriggerListByUserId(String userId) {
////		List<GatewayTriggerRecord> triggerList = localTriggerRecordDao.getTriggerListByUserId(userId);
////
////		return triggerList;
////	}
////
////	public List<GatewayTriggerRecord> getDeleteTriggerListByUserId(Long userId) {
////		List<GatewayTriggerRecord> triggerList = localTriggerRecordDao.getDeleteTriggerListByUserId(userId);
////
////		return triggerList;
////	}
////
////
//
//
//
//}
