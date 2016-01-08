package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.trigger.ClientTriggerResult;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class ClientTriggerResultDao extends AbstractDataAccess<ClientTriggerResult>{




	@Override
	protected Class<ClientTriggerResult> getTypeCls() {
		return ClientTriggerResult.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("clienTriggerResult");
	}
}
