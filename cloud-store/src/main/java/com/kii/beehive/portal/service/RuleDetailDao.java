package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.RuleDetail;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class RuleDetailDao extends AbstractDataAccess<RuleDetail> {




	@Override
	protected Class<RuleDetail> getTypeCls() {
		return RuleDetail.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("RuleDetail");
	}


}
