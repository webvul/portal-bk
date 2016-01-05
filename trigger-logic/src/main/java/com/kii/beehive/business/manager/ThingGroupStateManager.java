package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.service.ThingTagService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.GroupTriggerStatusDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.KiiTriggerCol;
import com.kii.beehive.portal.store.entity.trigger.MemberState;
import com.kii.beehive.portal.store.entity.trigger.TriggerGroupPolicy;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.condition.NotLogic;

@Component
public class ThingGroupStateManager {


	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private GroupTriggerStatusDao statusDao;

	@Autowired
	private ThingTagService thingTagService;

	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private KiicloudEventListenerService listenerService;

	@Autowired
	private AppInfoManager appInfoManager;



	private void initGroupState(List<String> thingList){


		thingList.forEach(thingID->{

			ThingStatus status=thingIFService.getStatus(thingID);

			thingIFService.putStatus(thingID,status);

		});

	}



	public void createThingGroup( GroupTriggerRecord record){

		String triggerID=triggerDao.addKiiEntity(record);

		List<GlobalThingInfo>  thingList=thingTagService.getThingInfos(record.getSource().getSelector());


		GroupTriggerRuntimeState state=new GroupTriggerRuntimeState();
		state.setId(triggerID);

		TriggerGroupPolicy  policy=record.getPolicy();

		state.setPolicy(policy.getGroupPolicy());
		state.setCriticalNumber(policy.getCriticalNumber());
		state.setWhenType(record.getPerdicate().getTriggersWhen());

		List<String> thingIDs=thingList.stream().map(GlobalThingInfo::getFullKiiThingID).collect(Collectors.toList());
		thingIDs.forEach(thingID-> {
			KiiTriggerCol idCol=registDoubleTrigger(thingID, record.getPerdicate().getCondition(),triggerID);
			state.addThingTriggerInfo(thingID,idCol);
		});

		statusDao.addEntity(state,triggerID);
		initGroupState(thingIDs);

		if(!record.getSource().getSelector().getTagList().isEmpty()) {
			listenerService.addTagChangeListener(record.getSource().getSelector().getTagList(),triggerID);
		}
	}


	private KiiTriggerCol registDoubleTrigger(String thingID, Condition condition, String triggerID){

		ThingIDTools.ThingIDCombine combine=ThingIDTools.splitFullKiiThingID(thingID);

		KiiTriggerCol idCol=new KiiTriggerCol();

		StatePredicate positivePredicate=new StatePredicate();
		positivePredicate.setCondition(condition);
		positivePredicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);

		ThingTrigger triggerTrue=new ThingTrigger();

		triggerTrue.setPredicate(positivePredicate);
		triggerTrue.setServiceCode(getPositiveServiceCode(combine,triggerID));
		String positiveID=thingIFService.createTrigger(thingID,triggerTrue);
		idCol.setPositiveID(positiveID);

		StatePredicate negativePredicate=new StatePredicate();
		negativePredicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);
		NotLogic negCond=new NotLogic();
		negCond.setClause(condition);
		negativePredicate.setCondition(ConditionBuilder.notCondition(condition).getConditionInstance());

		ThingTrigger triggerFalse=new ThingTrigger();

		triggerFalse.setPredicate(negativePredicate);
		triggerFalse.setServiceCode(getNegativeServiceCode(combine,triggerID));

		String negativeID=thingIFService.createTrigger(thingID,triggerFalse);
		idCol.setNegativeID(negativeID);

		return idCol;

	}

	private  ServiceCode getPositiveServiceCode(ThingIDTools.ThingIDCombine combine, String triggerID){

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint(EndPointNameConstant.PositiveTriggerEndPoint);
		serviceCode.addParameter("thingID",combine.kiiThingID);
		serviceCode.addParameter("triggerID",triggerID);
		serviceCode.setTargetAppID(combine.kiiAppID);
		serviceCode.setExecutorAccessToken(appInfoManager.getDefaultOwer(combine.kiiAppID).getAppAuthToken());

		return serviceCode;
	}

	private  ServiceCode getNegativeServiceCode(ThingIDTools.ThingIDCombine combine,String triggerID){

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint(EndPointNameConstant.NegitiveTriggerEndPoint);
		serviceCode.addParameter("thingID",combine.kiiThingID);
		serviceCode.addParameter("triggerID",triggerID);
		serviceCode.setTargetAppID(combine.kiiAppID);
		serviceCode.setExecutorAccessToken(appInfoManager.getDefaultOwer(combine.kiiAppID).getAppAuthToken());

		return serviceCode;
	}

	public void updateThingGroup(List<GlobalThingInfo>  thingList,String triggerID){

		List<String> thingIDs=thingList.stream().map(GlobalThingInfo::getFullKiiThingID).collect(Collectors.toList());

		GroupTriggerRecord  triggerRecord= (GroupTriggerRecord) triggerDao.getObjectByID(triggerID);

		GroupTriggerRuntimeState state=statusDao.getObjectByID(triggerID);

		MemberState memberMap=state.getMemberState();

		Set<String> oldIDs=memberMap.getDeletedIDs(thingIDs);

		Map<String,KiiTriggerCol> map=state.getCurrThingTriggerMap();

		//remove trigger

		oldIDs.forEach(id->{
			KiiTriggerCol idCol=map.remove(id);
			thingIFService.removeTrigger(id,idCol.getNegativeID());
			thingIFService.removeTrigger(id,idCol.getPositiveID());
		});
		//add new trigger
		Set<String> newIDs=state.getMemberState().getAddedIDs(thingIDs);

		newIDs.forEach(thingID-> {
			KiiTriggerCol idCol=registDoubleTrigger(thingID, triggerRecord.getPerdicate().getCondition(),triggerID);
			map.put(thingID,idCol);
		});

		state.setCurrThingTriggerMap(map);
		state.setMemberState(memberMap);

		statusDao.updateEntityAllWithVersion(state,state.getVersion());

		initGroupState(new ArrayList<>(newIDs));
	}

}
