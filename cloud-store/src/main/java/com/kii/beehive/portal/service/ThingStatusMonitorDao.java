package com.kii.beehive.portal.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",bindUser=true )
@Component
public class ThingStatusMonitorDao extends AbstractDataAccess<ThingStatusMonitor>{


	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("thingStatusMonitor");
	}
	
	
	public List<ThingStatusMonitor> getMonitorsByQuery(MonitorQuery monitorQuery,KiiBucketPager pager){
		
		
		QueryParam query=monitorQuery.generQuery();
		
		return super.pagerQuery(query,pager);
		
	}
	
	
	
	
	public static class MonitorQuery{
		
		private String name;
		
		private Set<String> things=new HashSet<>();
		
		private Boolean enable;
		
		private Date createTimeStart;
		
		private Date createTimeEnd;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Set<String> getThings() {
			return things;
		}
		
		public void setThings(Set<String> things) {
			this.things = things;
		}
		
		public Boolean isEnable() {
			return enable;
		}
		
		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
		
		public void setCreateTimeStart(Date createTimeStart) {
			this.createTimeStart = createTimeStart;
		}
		
		public void setCreateTimeEnd(Date createTimeEnd) {
			this.createTimeEnd = createTimeEnd;
		}
		
		public QueryParam  generQuery(){
			
			ConditionBuilder builder=ConditionBuilder.andCondition();
			if(StringUtils.isNotBlank(name)){
				builder.prefixLike("name",name);
			}
			if(enable!=null){
				builder.equal("status", enable?ThingStatusMonitor.MonitorStatus.enable: ThingStatusMonitor.MonitorStatus.disable);
			}else{
				builder.In("status",new Object[]{ThingStatusMonitor.MonitorStatus.enable, ThingStatusMonitor.MonitorStatus.disable});
			}
			
			if(!things.isEmpty()){
				ConditionBuilder sub=ConditionBuilder.orCondition();
				for(String id:things){
					sub.equal("vendorThingIDs."+id,true);
				}
				builder.addSubClause(sub);
			}
			if(createTimeStart!=null||
					createTimeEnd!=null){
				
				if(createTimeStart!=null){
					builder.greatAndEq("_created",createTimeStart);
				}
				if(createTimeEnd!=null){
					builder.lessAndEq("_created",createTimeEnd);
				}
			}
			return builder.getFinalQueryParam();
		}
	}
}
