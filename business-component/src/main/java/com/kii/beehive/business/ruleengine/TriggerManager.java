package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.service.OperateLogDao;
import com.kii.beehive.portal.store.entity.OperateLog;
import com.kii.extension.ruleengine.TriggerCreateException;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

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

	@Value("${spring.profile:test}")
	private String profile;

	@PostConstruct
	public void init() {

		if( "local".equals(profile) ) {
			return ;
		}

		List<TriggerRecord> recordList = triggerDao.getAllEnableTrigger();

		List<TriggerRecord>  list=recordList.stream().filter((r)->r.getType()!= BeehiveTriggerType.Gateway).collect(Collectors.toList());
		
		List<String> errList=creator.init(list);

		errList.forEach(err->triggerDao.deleteTriggerRecord(err,"create trigger fail in system init "));
		
	}


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
				
				creator.createTrigger(record);
			}
		}catch(TriggerCreateException ex){
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
			
			creator.removeTrigger(record);
			
			record.fillCreator(record.getUserID());
			creator.createTrigger(record);
			
			triggerDao.updateEntity(record, record.getId());
		}
		return record;
		
	}


	public Map<String, Object> getRuleEngingDump() {

		return creator.getRuleEngingDump(null);
	}

	public Map<String, Object> getRuleEngingDump(String triggerID) {

		return creator.getRuleEngingDump(triggerID);
	}

	public void disableTrigger(String triggerID) {
		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.disableTrigger(triggerID);

		logTool.triggerLog(record, OperateLog.ActionType.disable);


		if(record.getType()==BeehiveTriggerType.Gateway){
			
			gwOperate.disableTrigger((GatewayTriggerRecord) record);
		}else {

			creator.disableTrigger(record);

		}
	}


	public void enableTrigger(String triggerID) {


		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.enableTrigger(triggerID);

		logTool.triggerLog(record, OperateLog.ActionType.enable);


		if(record.getType()==BeehiveTriggerType.Gateway){

			gwOperate.enableTrigger((GatewayTriggerRecord) record);

		}else {

			creator.createTrigger(record);

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
		
		
		Set<String> triggerSet=creator.getTriggerListByThingID(thingId);
		
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
			creator.removeTrigger(record);
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


}
