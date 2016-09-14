package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.helper.TriggerCreator;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.CommandToThingInGW;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummarySource;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.SimpleCondition;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingOfKiiCloud;

@Component
public class TriggerManager {

	private static final Logger log = LoggerFactory.getLogger(TriggerManager.class);

	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private AppInfoManager appInfoManager;

	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TriggerCreator creator;


	public void reinit() {
		creator.clear();

		init();
	}

	@PostConstruct
	public void init() {
		creator.init();
	}


	public TriggerRecord createTrigger(TriggerRecord record) {


		if(checkLocalRule(record)){
			try {
				return createGatewayRecord((SummaryTriggerRecord) record);
			}catch(IllegalStateException e){
				log.warn("invalid gateway trigger param");
			}
		}

		triggerDao.addKiiEntity(record);

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {
			creator.addTriggerToEngine(record);
		}
		return record;


	}


	public TriggerRecord updateTrigger(TriggerRecord record) {

		TriggerRecord oldRecord = this.getTriggerByID(record.getId());

		if(oldRecord.getType().equals(BeehiveTriggerType.Gateway)){

			if( ! checkLocalRule(record) ){
				//
				throw new IllegalStateException();

			}else {

				GatewayTriggerRecord gatewayTriggerRecord = convertToGatewayTriggerRecord((SummaryTriggerRecord) record);

				triggerDao.updateEntity(gatewayTriggerRecord, record.getId());

				sendGatewayCommand(gatewayTriggerRecord,GatewayCommand.updateTrigger);

				return gatewayTriggerRecord;
			}


		}else {

			record.setRecordStatus(TriggerRecord.StatusType.enable);

			this.deleteTrigger(record.getTriggerID());

			creator.addTriggerToEngine(record);

			triggerDao.updateEntity(record, record.getId());

			return record;
		}

	}


	private boolean checkLocalRule(TriggerRecord record) {

		if( ! (record instanceof SummaryTriggerRecord
				&& record.getPredicate().getTriggersWhen().equals(WhenType.CONDITION_TRUE)
				&& record.getPredicate().getSchedule() == null
				&& ( record.getPredicate().getCondition()  instanceof AndLogic)
				&& ( (AndLogic) record.getPredicate().getCondition() ).getClauses().size() == 2
				&& ( (AndLogic) record.getPredicate().getCondition() ).getClauses().get(0) instanceof AndLogic
				&& ( (AndLogic) ( ( (AndLogic) record.getPredicate().getCondition() ).getClauses().get(0) ) ).getClauses().size() == 2
			    && ( (AndLogic) ( ( (AndLogic) record.getPredicate().getCondition() ).getClauses().get(0) ) ).getClauses().get(0) instanceof SimpleCondition
			) ) {


			return false;
		}

		SummaryTriggerRecord  summaryTriggerRecord =(SummaryTriggerRecord)record;

		//source only one thing
		Collection<SummarySource> sourceCollection = summaryTriggerRecord.getSummarySource().values();
		if(sourceCollection.size() != 1 ){
			return false;
		}
		TagSelector selector = sourceCollection.iterator().next().getSource();
		if( ( selector.getTagList() != null && selector.getTagList().size() > 0 )
				||  ( selector.getThingList() != null && selector.getThingList().size() != 1 )){
			return false;
		}



		return true ;
	}


	private GatewayTriggerRecord createGatewayRecord(SummaryTriggerRecord  summaryRecord){


		GatewayTriggerRecord gatewayTriggerRecord = convertToGatewayTriggerRecord(summaryRecord);

		triggerDao.addKiiEntity(gatewayTriggerRecord);

		sendGatewayCommand(gatewayTriggerRecord,GatewayCommand.createTrigger);

		return gatewayTriggerRecord;

	}

	private GatewayTriggerRecord convertToGatewayTriggerRecord(SummaryTriggerRecord summaryRecord) {
		GatewayTriggerRecord gatewayTriggerRecord = new GatewayTriggerRecord();

		BeanUtils.copyProperties(summaryRecord, gatewayTriggerRecord);

		gatewayTriggerRecord.setRecordStatus(TriggerRecord.StatusType.enable);
//		gatewayTriggerRecord.setPreparedCondition(summaryRecord.getPreparedCondition());
		//
//		SimpleCondition condition = (SimpleCondition)( (AndLogic) ( ( (AndLogic) summaryRecord.getPredicate().getCondition() ).getClauses().get(0) ) ).getClauses().get(0);
//		//process "field": "EnvironmentSensor.Bri",
//		condition.setField(condition.getField().substring(condition.getField().indexOf(".")+1));
//		gatewayTriggerRecord.setPredicate(summaryRecord.getPredicate());
//		gatewayTriggerRecord.getPredicate().setCondition(condition);
//
//		gatewayTriggerRecord.setTargetParamList(summaryRecord.getTargetParamList());
//
//		gatewayTriggerRecord.setUserID(summaryRecord.getUserID());
//		gatewayTriggerRecord.setDescription(summaryRecord.getDescription());

		Collection<SummarySource> sourceCollection = summaryRecord.getSummarySource().values();
		TagSelector selector = sourceCollection.iterator().next().getSource();

		GlobalThingInfo sourceThing = globalThingDao.findByID(selector.getThingList().get(0));
		gatewayTriggerRecord.getSource().getVendorThingIdList().add(sourceThing.getVendorThingID());
		gatewayTriggerRecord.getSource().getThingList().add(sourceThing.getId());
		//
		ThingOfKiiCloud gatewayOfKiiCloud = null;
		try {
			gatewayOfKiiCloud = thingIFService.getThingGateway(sourceThing.getFullKiiThingID());
		} catch (Exception e) {
			throw new IllegalStateException();
		}
		String thingID=gatewayOfKiiCloud.getThingID();

		String fullKiiThingID= ThingIDTools.joinFullKiiThingID(sourceThing.getKiiAppID(), thingID);

		List<EndNodeOfGateway> allEndNodesOfGateway = thingIFService.getAllEndNodesOfGateway(fullKiiThingID);
		Map<String, EndNodeOfGateway> allEndNodesOfGatewayMap = new HashMap<>();
		allEndNodesOfGateway.forEach(endNodeOfGateway -> allEndNodesOfGatewayMap.put(endNodeOfGateway.getVendorThingID(), endNodeOfGateway));

		List<ExecuteTarget> targets = summaryRecord.getTargets();

//		targets:
		for(ExecuteTarget target:targets)
			switch (target.getType()) {

				case "ThingCommand":
					CommandToThing command = (CommandToThing) target;
					CommandToThingInGW cmdInGW = new CommandToThingInGW();
					cmdInGW.setCommand(command.getCommand());
//					cmdInGW.getSelector().setVendorThingIdList(new ArrayList<>());
//					cmdInGW.getSelector().setThingList(new ArrayList<>());
					Set<GlobalThingInfo> thingList = thingTagService.getThingInfos(command.getSelector());

					for (GlobalThingInfo thing : thingList) {
						if (allEndNodesOfGatewayMap.get(thing.getVendorThingID()) == null) {
							throw new IllegalStateException();
						}
						cmdInGW.getSelector().getVendorThingIdList().add(thing.getVendorThingID());
						cmdInGW.getSelector().getThingList().add(thing.getId());
					}
					gatewayTriggerRecord.addTarget(cmdInGW);
					break;
				case "HttpApiCall":
					throw new IllegalStateException();

			}


//		String thingID="th.f83120e36100-a269-5e11-bf4b-0c5b4813";
		String vendorThingID=globalThingDao.getThingByFullKiiThingID(sourceThing.getKiiAppID(), thingID).getVendorThingID();
		gatewayTriggerRecord.setGatewayVendorThingID(vendorThingID);
		gatewayTriggerRecord.setGatewayFullKiiThingID(fullKiiThingID);
		return gatewayTriggerRecord;
	}

	public Map<String, Object> getRuleEngingDump() {

		return creator.getRuleEngingDump();
	}


	public void disableTrigger(String triggerID) {
		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.disableTrigger(triggerID);

		if(record.getType()==BeehiveTriggerType.Gateway){

			sendGatewayCommand((GatewayTriggerRecord) record, GatewayCommand.disableTrigger);

		}else {

			creator.disableTrigger(record);

		}
	}


	public void enableTrigger(String triggerID) {


		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.enableTrigger(triggerID);

		if(record.getType()==BeehiveTriggerType.Gateway){

			sendGatewayCommand((GatewayTriggerRecord) record, GatewayCommand.enableTrigger);

		}else {

			creator.addTriggerToEngine(record);

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

	public List<SimpleTriggerRecord> getTriggerListByUserIdAndThingId(Long userId, String thingId) {
		List<SimpleTriggerRecord> resultTriggerList = new ArrayList<SimpleTriggerRecord>();
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByUserId(userId);
		for (TriggerRecord trigger : triggerList) {
			if (trigger instanceof SimpleTriggerRecord) {
				SimpleTriggerRecord simpleTriggerRecord = (SimpleTriggerRecord) trigger;

				if (simpleTriggerRecord.getSource() == null) {
					continue;
				}
				String currThingId = simpleTriggerRecord.getSource().getThingID() + "";
				if (thingId.equals(currThingId)) {
					resultTriggerList.add(simpleTriggerRecord);
				}
			}
		}

		return resultTriggerList;
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

		if(record.getType()==BeehiveTriggerType.Gateway){

			sendGatewayCommand((GatewayTriggerRecord) record, GatewayCommand.deleteTrigger);

		}else {

				creator.removeTrigger(record);
		}

		triggerDao.deleteTriggerRecord(triggerID);

	}


	public List<TriggerRecord> getAllGatewayTrigger() {
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByType(BeehiveTriggerType.Gateway);

		return triggerList;
	}
	public List<TriggerRecord> getTriggerListByGatewayVendorThingID(String vendorThingID) {
		List<TriggerRecord> triggerList = triggerDao.getTriggerListByGatewayVendorThingID(vendorThingID);

		return triggerList;
	}


	private enum GatewayCommand{

		deleteTrigger,disableTrigger,enableTrigger,createTrigger,updateTrigger;
	}


	private  void sendGatewayCommand(GatewayTriggerRecord  record,GatewayCommand act ) {


		String triggerID=record.getTriggerID();

		String fullThingID=record.getGatewayFullKiiThingID();

		Map<String, Action> actions = new HashMap<>();
		Action action = new Action();
		actions.put(act.name(), action);

		if(act == GatewayCommand.createTrigger || act == GatewayCommand.updateTrigger){
			action.setField("triggerJson", record);
		}else {
			action.setField("triggerID", triggerID);
		}
		//command								send to gateway
		ThingCommand command = new ThingCommand();
		command.setSchema("gateway");
		command.setTitle("trigger");
		//action
		command.setActions(Arrays.asList(actions));

		ThingIDTools.ThingIDCombine combine = ThingIDTools.splitFullKiiThingID(fullThingID);

		command.setUserID(appInfoManager.getDefaultOwer(combine.kiiAppID).getUserID());
		thingIFService.sendCommand(command, fullThingID);

	}

}
