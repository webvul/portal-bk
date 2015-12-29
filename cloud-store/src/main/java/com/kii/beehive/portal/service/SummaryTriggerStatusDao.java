package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRuntimeState;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class SummaryTriggerStatusDao extends AbstractDataAccess<SummaryTriggerRuntimeState> {
	@Override
	protected Class<SummaryTriggerRuntimeState> getTypeCls() {
		return SummaryTriggerRuntimeState.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("summaryTriggerRuntimeState");
	}




	public void saveCurrThingIDs(Map<String,String> thingTriggerMap,String thingID, String triggerID){

		Map<String,Object> param= new HashMap<>();

		param.put("currThingTriggerMap",thingTriggerMap);
		param.put("summaryThingID",thingID);

		super.updateEntity(param,triggerID);

	}
}
