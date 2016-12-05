package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="master",appBindSource="propAppBindTool",bindUser=true )
@Component
public class ThingStatusMonitorDao extends AbstractDataAccess<ThingStatusMonitor>{


	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("thingStatusMonitor", ScopeType.Me,"me");
	}
}
