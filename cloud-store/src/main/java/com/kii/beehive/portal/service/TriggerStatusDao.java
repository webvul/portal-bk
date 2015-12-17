package com.kii.beehive.portal.service;

import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName = "portal",appBindSource="propAppBindTool")
public class TriggerStatusDao extends AbstractDataAccess<TriggerRuntimeState> {


	public void setGroupMemberStatus(String thingID,boolean sign){


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
