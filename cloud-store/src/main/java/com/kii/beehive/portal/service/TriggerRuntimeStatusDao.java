package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.SummaryStateEntry;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRuntimeState;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName = "portal",appBindSource="propAppBindTool")
public class TriggerRuntimeStatusDao extends AbstractDataAccess<TriggerRuntimeState> {


	public void setGroupMemberStatus(String thingID,boolean sign,String triggerID){


		Map<String,Object> param=Collections.singletonMap("member-"+thingID,sign);

		super.updateEntity(param,triggerID);
	}

	public void saveState(TriggerRuntimeState state, String triggerID) {

		super.addEntity(state,triggerID);
	}

	public void saveCurrThingIDs(Map<String,?> thingTriggerMap, String triggerID){

		Map<String,Object> param= Collections.singletonMap("currThingTriggerMap",thingTriggerMap);

		super.updateEntity(param,triggerID);

	}

	public void updateSummary(String groupName,SummaryStateEntry entry,String id){

		Map<String,Object>  map=Collections.singletonMap(groupName, entry);

		super.updateWithVerify(id,map,5);
	}

	public GroupTriggerRuntimeState getGroupRuntimeState(String triggerID){
		return (GroupTriggerRuntimeState) getObjByID(triggerID);
	}


	public SimpleTriggerRuntimeState getSimpleRuntimeState(String triggerID){
		return (SimpleTriggerRuntimeState) getObjByID(triggerID);
	}


	public SummaryTriggerRuntimeState getSummaryRuntimeState(String triggerID){
		return (SummaryTriggerRuntimeState) getObjByID(triggerID);
	}


	public  TriggerRuntimeState getObjByID(String id){

		try{
			return super.getObjectByID(id);
		}catch(ObjectNotFoundException e){
			return null;
		}
	}

	public List<TriggerRuntimeState> getUnCompletedList(BeehiveTriggerType type){

		QueryParam query= ConditionBuilder.orCondition().equal("type", type).getFinalQueryParam();

		return super.query(query);

	}

	@Override
	protected Class<TriggerRuntimeState> getTypeCls() {
		return TriggerRuntimeState.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerRuntimeState");
	}
	

}
