package com.kii.beehive.portal.manager;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.service.BusinessTriggerService;
import com.kii.beehive.business.service.CommandExecuteService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerTarget;

@Component
public class SimpleThingTriggerManager {



	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private ThingStateManager thingTagService;

	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private AppInfoManager appInfoManager;

	@Autowired
	private TriggerRuntimeStatusDao statusDao;

	@Autowired
	private BusinessTriggerService  triggerService;


	@Autowired
	private CommandExecuteService commandService;

//	@PostConstruct
	public void refreshState(){

		List<TriggerRuntimeState> stateList=statusDao.getUnCompletedList(BeehiveTriggerType.Simple);

		stateList.forEach(state->{

			SimpleTriggerRuntimeState simpleState=(SimpleTriggerRuntimeState)state;

			String thingID=simpleState.getThingIDSet().iterator().next();
			ThingStatus status=thingIFService.getStatus(thingID);

			thingIFService.putStatus(thingID,status);

		});

	}


	public String  createSimpleTrigger(SimpleTriggerRecord record){

		record.setRecordStatus(TriggerRecord.StatusType.disable);
		String triggerID=triggerDao.addEntity(record).getObjectID();

		SimpleTriggerRecord.ThingID thingID=record.getSource();

		GlobalThingInfo thing=thingTagService.getThingByID(thingID.getThingID());

		triggerService.registerBusinessTrigger(Collections.singleton(thing.getFullKiiThingID()),record.getId(), (StatePredicate) record.getPredicate().getPredicate());

		triggerDao.enableTrigger(triggerID);
		return triggerID;
	}

	public void removeSimpleTrigger(String triggerID){

		SimpleTriggerRuntimeState state=statusDao.getSimpleRuntimeState(triggerID);

		thingIFService.removeTrigger(state.getThingIDSet().iterator().next(),state.getTriggerID());

		statusDao.removeEntity(triggerID);

		triggerDao.deleteTriggerRecord(triggerID);


	}


	private  ServiceCode getSimpleServiceCode(String thingID,String triggerID){

		ThingIDTools.ThingIDCombine  info= ThingIDTools.splitFullKiiThingID(thingID);

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint(EndPointNameConstant.SimpleTriggerEndPoint);
		serviceCode.addParameter("thingID",thingID);
		serviceCode.addParameter("triggerID",triggerID);

		FederatedAuthResult result=appInfoManager.getDefaultOwer(info.kiiAppID);
		serviceCode.setExecutorAccessToken(result.getAppAuthToken());

		return serviceCode;
	}


//	private void registSingleTrigger(String thingID,SimpleTriggerRecord record){
//
//
//
//	}

	private void registSingleTriggerOld(String thingID, StatePredicate predicate, String triggerID){


		ThingTrigger triggerInfo=new ThingTrigger();

		triggerInfo.setTitle(triggerID);

		triggerInfo.setTarget(TriggerTarget.SERVER_CODE);
		triggerInfo.setPredicate(predicate);
		triggerInfo.setServiceCode(getSimpleServiceCode(thingID,triggerID));

		String kiiTriggerID=thingIFService.createTrigger(thingID,triggerInfo);

		SimpleTriggerRuntimeState state=new SimpleTriggerRuntimeState();
		state.setTriggerID(kiiTriggerID);
		state.addThingID(thingID);

		statusDao.saveState(state,triggerID);
	}
	
	public void onConditionMatch(String thingID, String triggerID) {
		commandService.doCommand(triggerID);

	}
}
