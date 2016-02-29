package com.kii.beehive.business.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.service.BusinessTriggerService;
import com.kii.beehive.business.service.CommandExecuteService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.BusinessTrigger;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TriggerGroupPolicy;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.UpdateResponse;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;

@Component
public class ThingGroupStateManager {


	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private TriggerRuntimeStatusDao statusDao;

	@Autowired
	private ThingStateManager thingTagService;

	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Autowired
	private AppInfoManager appInfoManager;

	@Autowired
	private BusinessTriggerService triggerService;


	@Autowired
	private CommandExecuteService commandService;

	public void removeTrigger(String triggerID){


		GroupTriggerRuntimeState state=statusDao.getGroupRuntimeState(triggerID);

		triggerService.removeTrigger(state.getBusinessTriggerID());

		listenerService.removeListener(state.getListenerID());

		triggerDao.deleteTriggerRecord(triggerID);
	}


	public String createThingGroup( GroupTriggerRecord record){

		record.setRecordStatus(TriggerRecord.StatusType.disable);
		String triggerID=triggerDao.addKiiEntity(record);

		List<GlobalThingInfo>  thingList=thingTagService.getThingInfos(record.getSource().getSelector());


		GroupTriggerRuntimeState state=new GroupTriggerRuntimeState();
		state.setId(triggerID);

		TriggerGroupPolicy  policy=record.getPolicy();

		state.setPolicy(policy.getGroupPolicy());
		state.setCriticalNumber(policy.getCriticalNumber());
		state.setWhenType(((StatePredicate)record.getPredicate().getPredicate()).getTriggersWhen());

		List<String> thingIDs=thingList.stream().map(GlobalThingInfo::getFullKiiThingID).collect(Collectors.toList());
		state.setThingIDSet(new HashSet<>(thingIDs));

		BusinessTrigger businessTrigger = getBusinessTrigger(record, triggerID, thingIDs);
		state.setBusinessTriggerID(businessTrigger.getId());

		if(!record.getSource().getSelector().getTagList().isEmpty()) {
			String listenerID=listenerService.addGroupTagChangeListener(record.getSource().getSelector().getTagList(),triggerID);
			state.setListenerID(listenerID);
		}

		UpdateResponse  resp=statusDao.addEntity(state,triggerID);
		state.setVersion(resp.getVersionValue());

		triggerDao.enableTrigger(triggerID);

		checkCondition(record,state,businessTrigger.getMemberStates().getMemberStatusMap());

		return triggerID;

	}

	private BusinessTrigger getBusinessTrigger(GroupTriggerRecord record, String triggerID, List<String> thingIDs) {
		StatePredicate positive=new StatePredicate();
		positive.setCondition(((StatePredicate)record.getPredicate().getPredicate()).getCondition());
		positive.setTriggersWhen(TriggerWhen.CONDITION_CHANGED);

		return triggerService.registerBusinessTrigger(thingIDs,triggerID,positive);
	}


	public void onTagChanged(GroupTriggerRecord record,GroupTriggerRuntimeState state){


		List<GlobalThingInfo>  thingList=thingTagService.getThingInfos(record.getSource().getSelector());


		Set<String> thingIDs=thingList.stream().map(GlobalThingInfo::getFullKiiThingID).collect(Collectors.toSet());


		BusinessTrigger  positionTrigger=triggerService.updateTrigger(thingIDs,state.getBusinessTriggerID());

		checkCondition(record, state, positionTrigger.getMemberStates().getMemberStatusMap());
	}

	private void checkCondition(GroupTriggerRecord record, GroupTriggerRuntimeState state, Map<String,Boolean> memberStatus) {

		state.checkPolicy(memberStatus);
		statusDao.updateEntityAllWithVersion(state,state.getVersion());

		if(state.isCurrentStatus()){
			commandService.doCommand(record);
		}
	}
	
	
	public void onStatusChange(String thingID, String triggerID) {

		GroupTriggerRuntimeState state=statusDao.getGroupRuntimeState(triggerID);


		BusinessTrigger  businessTrigger=triggerService.getTriggerByID(state.getBusinessTriggerID());


		GroupTriggerRecord record= (GroupTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		checkCondition(record,state,businessTrigger.getMemberStates().getMemberStatusMap());

	}
}
