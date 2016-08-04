package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.helper.TriggerCreator;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.beehive.portal.service.LocalTriggerRecordDao;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.CommandToThingInGW;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.condition.LogicCol;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingOfKiiCloud;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

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
	private LocalTriggerRecordDao localTriggerRecordDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private BusinessEventListenerService eventService;

	@Autowired
	private EngineService service;

	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private EventListenerDao eventListenerDao;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private TriggerCreator creator;


	public void reinit() {

		service.clear();
		scheduleService.clearTrigger();

		init();
	}

	@PostConstruct
	public void init() {
		List<TriggerRecord> recordList = triggerDao.getAllTrigger();


		scheduleService.startSchedule();

		recordList.forEach(record -> {

			try {
				if(record.getType()== BeehiveTriggerType.Gateway){
					return;
				}
				creator.addTriggerToEngine(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		List<ThingStatusInRule> initThings = new ArrayList<>();

		thingTagService.iteratorAllThingsStatus(s -> {
			if (StringUtils.isEmpty(s.getStatus())) {
				return;
			}
			ThingStatus status = null;
			try {
				status = mapper.readValue(s.getStatus(), ThingStatus.class);
			} catch (IOException e) {
				log.error("invalid thing " + s.getId() + " status ", e);
				return;
			}
			ThingStatusInRule info = new ThingStatusInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(status.getFields());

			initThings.add(info);

		});

		service.initThingStatus(initThings);


	}


	public TriggerRecord createTrigger(TriggerRecord record) {


		if(checkLocalRule(record)){
			try {
				return createGatewayRecord((GroupTriggerRecord) record);
			}catch(IllegalStateException e){
				log.warn("invalid gateway trigger param");
			}
		}

		triggerDao.addKiiEntity(record);

		creator.addTriggerToEngine(record);
		return record;


	}


	private boolean checkLocalRule(TriggerRecord record) {

		if( ! (record instanceof GroupTriggerRecord
				&& record.getPredicate().getTriggersWhen().equals(WhenType.CONDITION_TRUE)
				&& record.getPredicate().getSchedule() == null
				&& ! ( record.getPredicate().getCondition()  instanceof LogicCol) ) ) {

			return false;
		}

		GroupTriggerRecord  groupRecord=(GroupTriggerRecord)record;

		//source only one thing
		if( ( groupRecord.getSource().getTagList() != null && groupRecord.getSource().getTagList().size() > 0 )
				||  ( groupRecord.getSource().getThingList() != null && groupRecord.getSource().getThingList().size() > 1 )){
			return false;
		}



		return true ;
	}


	private GatewayTriggerRecord createGatewayRecord(GroupTriggerRecord  groupRecord){


		GatewayTriggerRecord triggerRecord = new GatewayTriggerRecord();
		triggerRecord.setPolicy(groupRecord.getPolicy());
		triggerRecord.setRecordStatus(groupRecord.getRecordStatus());
		triggerRecord.setPreparedCondition(groupRecord.getPreparedCondition());
		triggerRecord.setPredicate(groupRecord.getPredicate());
		triggerRecord.setTargetParamList(groupRecord.getTargetParamList());

		triggerRecord.setUserID(groupRecord.getUserID());
		triggerRecord.setDescription(groupRecord.getDescription());

		GlobalThingInfo sourceThing = globalThingDao.findByID(groupRecord.getSource().getThingList().get(0));
		triggerRecord.getSource().getVendorThingIdList().add(sourceThing.getVendorThingID());

		//
		ThingOfKiiCloud gatewayOfKiiCloud = thingIFService.getThingGateway(sourceThing.getFullKiiThingID());
		String thingID=gatewayOfKiiCloud.getThingID();

//		String thingID="th.f83120e36100-a269-5e11-bf4b-0c5b4813";
		String venderThingID=globalThingDao.getThingByFullKiiThingID(sourceThing.getKiiAppID(), thingID).getVendorThingID();

		String fullKiiThingID=ThingIDTools.joinFullKiiThingID(sourceThing.getKiiAppID(), thingID);

		List<EndNodeOfGateway> allEndNodesOfGateway = thingIFService.getAllEndNodesOfGateway(fullKiiThingID);
		Map<String, EndNodeOfGateway> allEndNodesOfGatewayMap = new HashMap<>();
		allEndNodesOfGateway.forEach(endNodeOfGateway -> allEndNodesOfGatewayMap.put(endNodeOfGateway.getVendorThingID(), endNodeOfGateway));

		List<ExecuteTarget> targets = groupRecord.getTargets();

//		targets:
		for(ExecuteTarget target:targets){
			switch (target.getType()) {

				case "ThingCommand":
					CommandToThing command=(CommandToThing)target;
					CommandToThingInGW cmdInGW=new CommandToThingInGW();
					cmdInGW.setCommand(command.getCommand());
					cmdInGW.getSelector().setVendorThingIdList(new ArrayList<>());
					Set<GlobalThingInfo> thingList = thingTagService.getThingInfos(command.getSelector());

					for (GlobalThingInfo thing : thingList){
						if( allEndNodesOfGatewayMap.get(thing.getVendorThingID()) == null ){
							throw new IllegalStateException();
						}
						cmdInGW.getSelector().getVendorThingIdList().add(thing.getVendorThingID());
					}
					triggerRecord.addTarget(cmdInGW);

					break;
				case "HttpApiCall":
					triggerRecord.addTarget(target);
					break;
			}
		}
		triggerRecord.setGatewayVendorThingID(venderThingID);
		triggerRecord.setGatewayFullKiiThingID(fullKiiThingID);
		triggerDao.addKiiEntity(triggerRecord);

		sendGatewayCommand(triggerRecord,GatewayCommand.createTrigger);

		return triggerRecord;

	}




	public Map<String, Object> getRuleEngingDump() {

		Map<String, Object> map = service.dumpEngineRuntime();


		map.put("schedule", scheduleService.dump());

		return map;
	}


	public void disableTrigger(String triggerID) {
		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.disableTrigger(triggerID);

		if(record.getType()==BeehiveTriggerType.Gateway){

			sendGatewayCommand((GatewayTriggerRecord) record, GatewayCommand.disableTrigger);

		}else {

			service.disableTrigger(triggerID);
		}
	}


	public void enableTrigger(String triggerID) {


		TriggerRecord  record= triggerDao.getTriggerRecord(triggerID);

		triggerDao.enableTrigger(triggerID);

		if(record.getType()==BeehiveTriggerType.Gateway){

			sendGatewayCommand((GatewayTriggerRecord) record, GatewayCommand.enableTrigger);

		}else {

			service.enableTrigger(triggerID);
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
			service.removeTrigger(triggerID);

			scheduleService.removeManagerTaskForSchedule(triggerID);

			List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
			for (EventListener eventListener : eventListenerList) {
				eventListenerDao.removeEntity(eventListener.getId());
			}
		}

		triggerDao.deleteTriggerRecord(triggerID);

	}

	private enum GatewayCommand{

		deleteTrigger,disableTrigger,enableTrigger,createTrigger;
	}


	private  void sendGatewayCommand(GatewayTriggerRecord  record,GatewayCommand act ) {


		String triggerID=record.getTriggerID();

		String fullThingID=record.getGatewayFullKiiThingID();

		Map<String, Action> actions = new HashMap<>();
		Action action = new Action();
		actions.put(act.name(), action);

		if(act==GatewayCommand.createTrigger){
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
