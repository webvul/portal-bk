package com.kii.extension.ruleengine.service;

import java.util.Collections;
import java.util.List;

import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.service.AbstractDataAccess;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;

@Component
@BindAppByName(appName = "portal",appBindSource="propAppBindTool")
public class TriggerRecordDao extends AbstractDataAccess<TriggerRecord> {



	@Override
	protected Class<TriggerRecord> getTypeCls() {
		return TriggerRecord.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerRecord");
	}

	public TriggerRecord getTriggerRecord(String id){

		QueryParam query= ConditionBuilder.andCondition().equal("_id",id).getFinalQueryParam();

		List<TriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list.get(0);

	}

	public List<TriggerRecord> getTriggerListByUserId(String userId){

		QueryParam query= ConditionBuilder.andCondition().equal("userID",userId).getFinalQueryParam();

		List<TriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list;

	}

	public void deleteTriggerRecord(String id){

		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.deleted), id);

	}



	public void enableTrigger(String triggerID) {

		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.enable), triggerID);

	}

	public void disableTrigger(String triggerID) {
		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.disable), triggerID);

	}

	public List<TriggerRecord> getAllTrigger() {

		QueryParam query= ConditionBuilder.orCondition().equal("recordStatus",TriggerRecord.StatusType.disable).equal("recordStatus", TriggerRecord.StatusType.enable).getFinalQueryParam();

		List<TriggerRecord> list=super.query(query);

		return list;
	}

	public List<TriggerRecord> getAllEnableTrigger() {

		QueryParam query= ConditionBuilder.newCondition().equal("recordStatus", TriggerRecord.StatusType.enable).getFinalQueryParam();

		List<TriggerRecord> list=super.query(query);
		while(query.getPaginationKey() != null ) {
			list.addAll(super.query(query));
		}

		return list;
	}
}