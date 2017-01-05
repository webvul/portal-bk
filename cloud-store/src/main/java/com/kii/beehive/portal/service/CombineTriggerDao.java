package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.MLTriggerCombine;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;


@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class CombineTriggerDao extends BaseKiicloudDao<MLTriggerCombine>{
	
	
	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("MLCombineTrigger");
	}
	
	
}
