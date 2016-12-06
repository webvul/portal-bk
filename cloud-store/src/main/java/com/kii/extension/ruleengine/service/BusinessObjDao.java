package com.kii.extension.ruleengine.service;

import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.BusinessObject;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName = "portal", appBindSource = "propAppBindTool")
public class BusinessObjDao extends AbstractDataAccess<BusinessObject> {


	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerBusinessObj");
	}


}