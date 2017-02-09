package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.MLTriggerCombine;
import com.kii.extension.sdk.entity.BucketInfo;


@PortalApp
@Component
public class CombineTriggerDao extends BaseKiicloudDao<MLTriggerCombine>{
	
	
	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("MLCombineTrigger");
	}
	
	
}
