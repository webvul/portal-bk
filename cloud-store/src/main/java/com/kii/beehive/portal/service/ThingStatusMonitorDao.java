package com.kii.beehive.portal.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.QueryParam;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",bindUser=true )
@Component
public class ThingStatusMonitorDao extends BaseKiicloudDao<ThingStatusMonitor>{


	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("thingStatusMonitor");
	}
	
	
	public List<ThingStatusMonitor> getMonitorsByQuery(QueryParam query,KiiBucketPager pager){
		
		return super.pagerQuery(query,pager);
		
	}
	
	
}
