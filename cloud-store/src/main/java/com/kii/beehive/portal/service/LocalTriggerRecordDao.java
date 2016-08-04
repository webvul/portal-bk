package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.GatewayTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName = "portal",appBindSource="propAppBindTool")
public class LocalTriggerRecordDao extends AbstractDataAccess<GatewayTriggerRecord> {



	@Override
	protected Class<GatewayTriggerRecord> getTypeCls() {
		return GatewayTriggerRecord.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("localTriggerRecord");
	}

	public GatewayTriggerRecord getTriggerRecord(String id){

		QueryParam query= ConditionBuilder.andCondition().equal("_id",id).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list.get(0);

	}

	public GatewayTriggerRecord getEnableTriggerRecord(String id){

		QueryParam query= ConditionBuilder.andCondition().equal("_id",id).equal("recordStatus", TriggerRecord.StatusType.enable).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list.get(0);

	}

	public List<GatewayTriggerRecord> getTriggerListByUserId(String userId){

		String[] params= new String[2];
		params[0] = TriggerRecord.StatusType.enable.name();
		params[1] = TriggerRecord.StatusType.disable.name();
		QueryParam query= ConditionBuilder.andCondition().equal("userID",userId).In("recordStatus",params).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list;

	}

	public List<GatewayTriggerRecord> getTriggerListByGatewayVendorThingID(String gatewayVendorThingID){

		String[] params= new String[2];
		params[0] = TriggerRecord.StatusType.enable.name();
		params[1] = TriggerRecord.StatusType.disable.name();
		QueryParam query= ConditionBuilder.andCondition().equal("gatewayVendorThingID",gatewayVendorThingID)
				.In("recordStatus",params).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list;

	}

	public List<GatewayTriggerRecord> getDeleteTriggerListByUserId(Long userId){

		QueryParam query= ConditionBuilder.andCondition().equal("userID",userId).equal("recordStatus", TriggerRecord.StatusType.deleted).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.query(query);

		if(list.isEmpty()){
			return null;
		}
		return list;

	}

	public void deleteTriggerRecord(String id){

		TriggerRecord record=getObjectByID(id);
		if(record.getRecordStatus()== TriggerRecord.StatusType.deleted){
			ObjectNotFoundException e= new ObjectNotFoundException();
			e.setBucketID("triggerRecord");
			e.setObjectID(id);
			throw e;
		}

		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.deleted), id);

	}

	public void clearTriggerRecord(String id){

		super.removeEntity(id);

	}

	public void enableTrigger(String triggerID) {

		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.enable), triggerID);

	}

	public void disableTrigger(String triggerID) {
		super.updateEntity(Collections.singletonMap("recordStatus", TriggerRecord.StatusType.disable), triggerID);

	}

	public List<GatewayTriggerRecord> getAllTrigger() {

		QueryParam query= ConditionBuilder.orCondition().equal("recordStatus",TriggerRecord.StatusType.disable).equal("recordStatus", TriggerRecord.StatusType.enable).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.fullQuery(query);

		return list;
	}

	public List<GatewayTriggerRecord> getAllEnableTrigger() {

		QueryParam query= ConditionBuilder.newCondition().equal("recordStatus", TriggerRecord.StatusType.enable).getFinalQueryParam();

		List<GatewayTriggerRecord> list=super.fullQuery(query);

		return list;
	}
}