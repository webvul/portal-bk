package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.BusinessTrigger;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class BusinessTriggerDao extends AbstractDataAccess<BusinessTrigger> {
	@Override
	protected Class<BusinessTrigger> getTypeCls() {
		return BusinessTrigger.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("businessTrigger");
	}
	
	public void enableTrigger(String triggerID) {

		super.updateEntity(Collections.singletonMap("enable",true),triggerID);
	}

	public BusinessTrigger getTriggerByID(String triggerID){

		QueryParam query= ConditionBuilder.andCondition().equal("_id",triggerID).equal("enable",true).getFinalQueryParam();

		List<BusinessTrigger> list= super.query(query);

		if(list.isEmpty()){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	public void updateThingState(String thingID, boolean sign,String triggerID,int version) {

		super.updateEntityWithVersion(Collections.singletonMap(thingID,sign),triggerID,version);

	}
	
	
	public void disableTrigger(String triggerID) {

		super.updateEntity(Collections.singletonMap("enable",false),triggerID);


	}
	
	public void addListenerID(String listenerID,String triggerID) {

		super.updateEntity(Collections.singletonMap("listenerID",listenerID),triggerID);


	}
	

}
