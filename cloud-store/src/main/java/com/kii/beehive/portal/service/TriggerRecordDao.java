package com.kii.beehive.portal.service;

import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRuntimeState;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

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
}