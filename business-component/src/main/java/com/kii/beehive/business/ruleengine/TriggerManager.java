package com.kii.beehive.business.ruleengine;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.service.OperateLogDao;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.OperateLog;
import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.GatewayTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;

@Component
public class TriggerManager {

	private static final Logger log = LoggerFactory.getLogger(TriggerManager.class);



	@Autowired
	private GatewayTriggerOperate gwOperate;
	
	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TriggerOperate creator;

	@Autowired
	private OperateLogDao logTool;

	public TriggerRecord createTrigger(TriggerRecord newTrigger) {
		
		logTool.triggerLog(newTrigger, OperateLog.ActionType.create);

		TriggerRecord record=gwOperate.addGatewayTrigger(newTrigger);
		
		if(record.getType()==BeehiveTriggerType.Gateway){
			return record;
		}
		
		triggerDao.addKiiEntity(record);
		
		try {
			if (record.getRecordStatus() == TriggerRecord.StatusType.enable) {
				
				logTool.triggerLog(record, OperateLog.ActionType.enable);
				
				String id=creator.createTrigger(record);
				
				triggerDao.updateEntity(Collections.singletonMap("relationTriggerID", id), record.getId());
			}
		}catch(TriggerException ex){
			logTool.triggerLog(record, OperateLog.ActionType.delete);
			
			triggerDao.removeEntity(record.getId());
			throw ex;
		}
		return record;


	}


	public TriggerRecord updateTrigger(TriggerRecord trigger) {

		TriggerRecord oldRecord = this.getTriggerByID(trigger.getId());

		logTool.triggerLog(trigger, OperateLog.ActionType.update);
		
		boolean isGW=oldRecord.getType()==BeehiveTriggerType.Gateway;
		boolean canGW=gwOperate.checkLocalRule(trigger);
		
		TriggerRecord record=gwOperate.updateGatewayTrigger(trigger,isGW,canGW);
		
		if(record.getType()!=BeehiveTriggerType.Gateway) {
			record.setRecordStatus(TriggerRecord.StatusType.enable);
			
			
			record.fillCreator(record.getUserID());
			record.setRelationTriggerID(oldRecord.getRelationTriggerID());
			
			creator.updateTrigger(record);
			
			triggerDao.updateEntity(record, record.getId());
		}
		return record;
		
	}



	public void disableTrigger(String triggerID) {
		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.disableTrigger(triggerID);

		logTool.triggerLog(record, OperateLog.ActionType.disable);


		if(record.getType()==BeehiveTriggerType.Gateway){
			
			gwOperate.disableTrigger((GatewayTriggerRecord) record);
		}else {

			creator.disableTrigger(record.getRelationTriggerID());

		}
	}


	public void enableTrigger(String triggerID) {


		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.enableTrigger(triggerID);

		logTool.triggerLog(record, OperateLog.ActionType.enable);


		if(record.getType()==BeehiveTriggerType.Gateway){

			gwOperate.enableTrigger((GatewayTriggerRecord) record);

		}else {

			creator.enableTrigger(record.getRelationTriggerID());

		}
	}

	public List<TriggerRecord> getTriggerListByUserId(Long userId) {
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByUserId(userId);

		return triggerList;
	}

	public List<TriggerRecord> getDeleteTriggerListByUserId(Long userId) {
		List<TriggerRecord> triggerList = triggerDao.getDeleteTriggerListByUserId(userId);

		return triggerList;
	}

	public List<TriggerRecord> getTriggerListByUserIdAndThingId(Long thingId) {
		
		String vendorThingID = thingTagService.getThingByID(thingId).getVendorThingID();
		Set<String> triggerSet = creator.getTriggerListByThingID(vendorThingID);
		
		return triggerDao.queryByIDSetForUser(triggerSet, AuthInfoStore.getUserID());

	}

	public TriggerRecord getTriggerByID(String triggerID) {

		TriggerRecord record = triggerDao.getTriggerRecord(triggerID);
		if (record == null) {
			throw EntryNotFoundException.tagNameNotFound(triggerID);
		}
		return record;
	}

	public void deleteTrigger(String triggerID) {


		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);
		
		triggerDao.deleteTriggerRecord(triggerID,"oper by user");
		
		logTool.triggerLog(record, OperateLog.ActionType.delete);

		if(record.getType()==BeehiveTriggerType.Gateway){

			gwOperate.deleteTrigger((GatewayTriggerRecord)record);
		}else {
			creator.removeTrigger(record.getRelationTriggerID());
		}


	}


	public List<TriggerRecord> getAllGatewayTrigger() {
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByType(BeehiveTriggerType.Gateway);

		return triggerList;
	}
	public List<TriggerRecord> getTriggerListByGatewayVendorThingID(String vendorThingID) {
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByGatewayVendorThingID(vendorThingID);

		return triggerList;
	}
	
	
	public void addBusinessData(BusinessDataObject obj) {
		
		this.creator.addBusinessData(obj);
	}

	
}
