package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

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

		QueryParam query= ConditionBuilder.newCondition().equal("recordStatus", TriggerRecord.StatusType.enable).getFinalQueryParam();

		List<TriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list.get(0);

	}



	public void deleteTriggerRecord(String id){

		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.deleted), id);

	}


	
	public void enableTrigger(String triggerID) {

		TriggerRecord record=super.getObjectByID(triggerID);
		if(record.getRecordStatus()== TriggerRecord.StatusType.disable) {
			super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.enable), triggerID);
		}
	}

	public void disableTrigger(String triggerID) {
		TriggerRecord record=super.getObjectByID(triggerID);
		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.disable), triggerID);
		}

	}
}
