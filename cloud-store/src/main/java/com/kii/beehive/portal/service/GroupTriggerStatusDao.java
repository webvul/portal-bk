package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName = "portal",appBindSource="propAppBindTool")
public class GroupTriggerStatusDao extends AbstractDataAccess<GroupTriggerRuntimeState> {


	public void setGroupMemberStatus(String thingID,boolean sign,String triggerID){


		Map<String,Object> param=Collections.singletonMap("member-"+thingID,sign);

		super.updateEntity(param,triggerID);
	}



	public void saveCurrThingIDs(Map<String,?> thingTriggerMap, String triggerID){

		Map<String,Object> param= Collections.singletonMap("currThingTriggerMap",thingTriggerMap);

		super.updateEntity(param,triggerID);

	}

	@Override
	protected Class<GroupTriggerRuntimeState> getTypeCls() {
		return GroupTriggerRuntimeState.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerRuntimeState");
	}
}
