package com.kii.beehive.business.ruleengine;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.helper.TriggerCreator;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.beehive.portal.service.LocalTriggerRecordDao;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.condition.LogicCol;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingOfKiiCloud;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@Component
public class LocalTriggerManager {

	private static final Logger log= LoggerFactory.getLogger(LocalTriggerManager.class);

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
	private TriggerCreator  creator;





	public ThingOfKiiCloud checkLocalRule(TriggerRecord record) {

//		List<GatewayOfKiiCloud> allEGateway = thingIFService.getAllEGateway();
//		for (GatewayOfKiiCloud gatewayOfKiiCloud : allEGateway){
//			List<EndNodeOfGateway> allEndNodesOfGateway = thingIFService.getAllEndNodesOfGateway(ThingIDTools.joinFullKiiThingID(gatewayOfKiiCloud.getKiiAppID(), gatewayOfKiiCloud.getThingID()));
//			System.out.println(allEndNodesOfGateway);
//		}

		ThingOfKiiCloud gatewayOfKiiCloud = null;
		boolean isLocalRule = false;
		if( record instanceof GroupTriggerRecord
				&& record.getPredicate().getTriggersWhen().equals(WhenType.CONDITION_TRUE)
				&& record.getPredicate().getSchedule() == null
				&& ! ( record.getPredicate().getCondition()  instanceof LogicCol ) ){//  local rule

			GroupTriggerRecord triggerRecord = (GroupTriggerRecord)record;

			//source only one thing
			if( ( triggerRecord.getSource().getTagList() != null && triggerRecord.getSource().getTagList().size() > 0 )
					||  ( triggerRecord.getSource().getThingList() != null && triggerRecord.getSource().getThingList().size() > 1 )){
				return gatewayOfKiiCloud;
			}

			GlobalThingInfo sourceThing = globalThingDao.findByID(triggerRecord.getSource().getThingList().get(0));
			triggerRecord.getSource().setVendorThingIdList(new ArrayList<>());
			triggerRecord.getSource().getVendorThingIdList().add(sourceThing.getVendorThingID());
			//
			gatewayOfKiiCloud = thingIFService.getThingGateway(sourceThing.getFullKiiThingID());
			gatewayOfKiiCloud.setKiiAppID(sourceThing.getKiiAppID());
			gatewayOfKiiCloud.setVendorThingID(globalThingDao.getThingByFullKiiThingID(gatewayOfKiiCloud.getKiiAppID(), gatewayOfKiiCloud.getThingID()).getVendorThingID());
			gatewayOfKiiCloud.setFullKiiThingID(ThingIDTools.joinFullKiiThingID(gatewayOfKiiCloud.getKiiAppID(), gatewayOfKiiCloud.getThingID()));
			List<EndNodeOfGateway> allEndNodesOfGateway = thingIFService.getAllEndNodesOfGateway(gatewayOfKiiCloud.getFullKiiThingID());
			Map<String, EndNodeOfGateway> allEndNodesOfGatewayMap = new HashMap<>();
			allEndNodesOfGateway.forEach(endNodeOfGateway -> allEndNodesOfGatewayMap.put(endNodeOfGateway.getVendorThingID(), endNodeOfGateway));

			List<ExecuteTarget> targets = triggerRecord.getTargets();

			targets:
			for(ExecuteTarget target:targets){
				switch (target.getType()) {

					case "ThingCommand":
						CommandToThing command=(CommandToThing)target;
						command.getSelector().setVendorThingIdList(new ArrayList<>());
						Set<GlobalThingInfo> thingList = thingTagService.getThingInfos(command.getSelector());

						for (GlobalThingInfo thing : thingList){
							if( allEndNodesOfGatewayMap.get(thing.getVendorThingID()) == null ){
								break targets;
							}
							command.getSelector().getVendorThingIdList().add(thing.getVendorThingID());
						}

						break;
					case "HttpApiCall":
//							CallHttpApi call=(CallHttpApi)target;
						break;
				}
			}
			isLocalRule = true;
			record.setTriggerPosition(TriggerRecord.TriggerPosition.local);
			record.setGatewayVendorThingID(gatewayOfKiiCloud.getVendorThingID());
			record.setGatewayFullKiiThingID(gatewayOfKiiCloud.getFullKiiThingID());
			localTriggerRecordDao.addKiiEntity(record);
		}
		return gatewayOfKiiCloud;
	}


	public void sendGatewayCommand(String fullThingID, Map<String, Action> actions ) {
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


	public void enableTrigger(String triggerID) {
		TriggerRecord triggerRecord = localTriggerRecordDao.getObjectByID(triggerID);

		if(triggerRecord ==null ) {
			ObjectNotFoundException e= new ObjectNotFoundException();
			e.setBucketID("localTriggerRecord");
			e.setObjectID(triggerID);
			throw e;
		}
		localTriggerRecordDao.enableTrigger(triggerID);

		//action
		Map<String, Action> actions = new HashMap<>();
		Action action = new Action();
		actions.put("enableTrigger", action);
		action.setField("triggerID", triggerID);

		sendGatewayCommand(triggerRecord.getGatewayFullKiiThingID(), actions);
	}


	public void disableTrigger(String triggerID) {

		TriggerRecord triggerRecord = localTriggerRecordDao.getObjectByID(triggerID);

		if(triggerRecord ==null ) {
			ObjectNotFoundException e= new ObjectNotFoundException();
			e.setBucketID("localTriggerRecord");
			e.setObjectID(triggerID);
			throw e;
		}
		localTriggerRecordDao.disableTrigger(triggerID);

		//action
		Map<String, Action> actions = new HashMap<>();
		Action action = new Action();
		actions.put("disableTrigger", action);
		action.setField("triggerID", triggerID);

		sendGatewayCommand(triggerRecord.getGatewayFullKiiThingID(), actions);

	}


	public void deleteTrigger(String triggerID) {

		TriggerRecord triggerRecord = localTriggerRecordDao.getObjectByID(triggerID);

		if(triggerRecord ==null ) {
			ObjectNotFoundException e= new ObjectNotFoundException();
			e.setBucketID("localTriggerRecord");
			e.setObjectID(triggerID);
			throw e;
		}
		localTriggerRecordDao.deleteTriggerRecord(triggerID);

		//action
		Map<String, Action> actions = new HashMap<>();
		Action action = new Action();
		actions.put("deleteTrigger", action);
		action.setField("triggerID", triggerID);

		sendGatewayCommand(triggerRecord.getGatewayFullKiiThingID(), actions);
	}


	public TriggerRecord getTriggerByID(String triggerID) {

		TriggerRecord record = localTriggerRecordDao.getTriggerRecord(triggerID);
		if (record == null) {
			throw  EntryNotFoundException.tagNameNotFound(triggerID);
		}
		return record;
	}


	public List<TriggerRecord> getAllTrigger() {
		List<TriggerRecord> triggerList = localTriggerRecordDao.getAllTrigger();

		return triggerList;
	}
	public List<TriggerRecord> getTriggerListByGatewayVendorThingID(String vendorThingID) {
		List<TriggerRecord> triggerList = localTriggerRecordDao.getTriggerListByGatewayVendorThingID(vendorThingID);

		return triggerList;
	}


	public List<TriggerRecord> getTriggerListByUserId(String userId) {
		List<TriggerRecord> triggerList = localTriggerRecordDao.getTriggerListByUserId(userId);

		return triggerList;
	}

	public List<TriggerRecord> getDeleteTriggerListByUserId(Long userId) {
		List<TriggerRecord> triggerList = localTriggerRecordDao.getDeleteTriggerListByUserId(userId);

		return triggerList;
	}





}
