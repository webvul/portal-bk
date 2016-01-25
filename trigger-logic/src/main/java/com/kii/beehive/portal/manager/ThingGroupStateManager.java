package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.service.BusinessTriggerRegister;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.KiiTriggerCol;
import com.kii.beehive.portal.store.entity.trigger.MemberState;
import com.kii.beehive.portal.store.entity.trigger.TriggerGroupPolicy;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.condition.AndLogic;
import com.kii.extension.sdk.query.condition.NotLogic;
import com.kii.extension.sdk.query.condition.OrLogic;
import com.kii.extension.sdk.query.condition.Range;

@Component
public class ThingGroupStateManager {


	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private TriggerRuntimeStatusDao statusDao;

	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Autowired
	private AppInfoManager appInfoManager;

	@Autowired
	private BusinessTriggerRegister triggerRegister;

//	@PostConstruct
	public void refreshState(){

		List<TriggerRuntimeState>  stateList=statusDao.getUnCompletedList(BeehiveTriggerType.Group);

		stateList.forEach(state->{

			GroupTriggerRuntimeState  groupState=(GroupTriggerRuntimeState)state;


			  initGroupState(groupState.getThingIDSet());


		});

	}


	private void initGroupState(Collection<String> thingList){


		thingList.forEach(thingID->{

			ThingStatus status=thingIFService.getStatus(thingID);

			thingIFService.putStatus(thingID,status);

		});

	}

	public void removeTrigger(String triggerID){


		GroupTriggerRuntimeState state=statusDao.getGroupRuntimeState(triggerID);

		for (Map.Entry<String, KiiTriggerCol> entry : state.getCurrThingTriggerMap().entrySet()) {

			String thingID=entry.getKey();
			KiiTriggerCol col=entry.getValue();

			thingIFService.removeTrigger(thingID,col.getNegativeID());
			thingIFService.removeTrigger(thingID,col.getPositiveID());
		}

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
		state.setWhenType(record.getPredicate().getTriggersWhen());

		List<String> thingIDs=thingList.stream().map(GlobalThingInfo::getFullKiiThingID).collect(Collectors.toList());
		thingIDs.forEach(thingID-> {
			state.addThingID(thingID);
			KiiTriggerCol idCol=registDoubleTrigger(thingID, record.getPredicate().getCondition(),triggerID);
			state.addThingTriggerInfo(thingID,idCol);
			state.getMemberState().setMemberStatus(thingID,false);
		});

		if(!record.getSource().getSelector().getTagList().isEmpty()) {
			String listenerID=listenerService.addGroupTagChangeListener(record.getSource().getSelector().getTagList(),triggerID);
			state.setListenerID(listenerID);
		}

		statusDao.addEntity(state,triggerID);
		initGroupState(thingIDs);

		triggerDao.enableTrigger(triggerID);

		return triggerID;

	}


	private KiiTriggerCol registDoubleTrigger(String thingID, Condition condition, String triggerID){


		KiiTriggerCol idCol=new KiiTriggerCol();

		String positiveID=thingIFService.createTrigger(thingID,getPositiveTrigger(condition,thingID,triggerID));
		idCol.setPositiveID(positiveID);

		String negativeID=thingIFService.createTrigger(thingID,getNegativeTrigger(condition,thingID,triggerID));
		idCol.setNegativeID(negativeID);

		return idCol;

	}

	private Condition getNotCondition(Condition condition){

		switch(condition.getType()){

			case and: {
				OrLogic result = new OrLogic();
				for (Condition cond : ((AndLogic) condition).getClauses()) {
					result.addClause(getNotCondition(cond));
				}
				return result;
			}
			case or: {
				AndLogic result = new AndLogic();
				for (Condition cond : ((OrLogic) condition).getClauses()) {
					result.addClause(getNotCondition(cond));
				}
				return result;
			}
			case range:{
				Range  range=(Range)condition;
				Range result=new Range();
				result.setField(range.getField());
				if(range.isUpperIncluded()!=null){
					result.setLowerIncluded(!range.isUpperIncluded());
				}else{
					result.setLowerIncluded(true);
				}
				if(range.isLowerIncluded()!=null){
					result.setUpperIncluded(!range.isLowerIncluded());
				}else{
					result.setUpperIncluded(true);
				}

				result.setLowerLimit(range.getUpperLimit());
				result.setUpperLimit(range.getLowerLimit());

				return result;
			}
			case eq:{

				NotLogic result=new NotLogic();
				result.setClause(condition);
				return result;
			}
			case not: {
				return ((NotLogic)condition).getClause();
			}
			default:
				throw new IllegalArgumentException("not supported this express:"+condition.getType());
		}


	}

	private  ThingTrigger getPositiveTrigger(Condition condition,String fullThingID, String triggerID){

		return getServiceCode(condition,EndPointNameConstant.PositiveTriggerEndPoint,fullThingID,triggerID);
	}

	private  ThingTrigger getNegativeTrigger(Condition condition,String fullThingID, String triggerID){

		return getServiceCode(getNotCondition(condition),EndPointNameConstant.NegativeTriggerEndPoint,fullThingID,triggerID);
	}

	private  ThingTrigger getServiceCode(Condition condition,String endPoint ,String fullThingID,String triggerID){

		ThingTrigger triggerForGroup=new ThingTrigger();

		StatePredicate negativePredicate=new StatePredicate();
		negativePredicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);
		negativePredicate.setCondition(condition);

		triggerForGroup.setTarget(TriggerTarget.SERVER_CODE);
		triggerForGroup.setPredicate(negativePredicate);


		ThingIDTools.ThingIDCombine combine=ThingIDTools.splitFullKiiThingID(fullThingID);

		String appName=combine.kiiAppID;
		ServiceCode serviceCode=new ServiceCode();


		serviceCode.setEndpoint(endPoint);
		serviceCode.addParameter("thingID",fullThingID);
		serviceCode.addParameter("triggerID",triggerID);
		serviceCode.setTargetAppID(appName);
		serviceCode.setExecutorAccessToken(appInfoManager.getDefaultOwer(appName).getAppAuthToken());

		triggerForGroup.setServiceCode(serviceCode);

		return triggerForGroup;
	}


	public void onTagChanged(String triggerID){


		GroupTriggerRecord record= (GroupTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		if(record==null){

			this.listenerService.disableTriggerByTargetID(triggerID);
			return;
		}

		List<GlobalThingInfo>  things=thingTagService.getThingInfos(record.getSource().getSelector());

		updateThingGroup(things,record);

	}

	private  void updateThingGroup(List<GlobalThingInfo>  thingList,GroupTriggerRecord triggerRecord){

		Set<String> thingIDs=thingList.stream().map(GlobalThingInfo::getFullKiiThingID).collect(Collectors.toSet());

		GroupTriggerRuntimeState state=statusDao.getGroupRuntimeState(triggerRecord.getId());
		if(state==null){
			listenerService.disableTriggerByTargetID(triggerRecord.getId());
			return;
		}

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
			KiiTriggerCol idCol=registDoubleTrigger(thingID, triggerRecord.getPredicate().getCondition(),triggerRecord.getId());
			map.put(thingID,idCol);
		});

		state.setCurrThingTriggerMap(map);
		state.setMemberState(memberMap);

		statusDao.updateEntityAllWithVersion(state,state.getVersion());

		initGroupState(new ArrayList<>(newIDs));
	}

}
