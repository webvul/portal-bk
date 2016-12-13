package com.kii.beehive.business.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import com.kii.beehive.portal.store.entity.ThingStatusMonitor;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.tools.AdditionFieldType;

public class MonitorQuery {
	
	private String name;
	
	private String  description;
	
	private Set<String> things=new HashSet<>();
	
	private Boolean enable;
	
	private Date createTimeStart;
	
	private Date createTimeEnd;
	
//	private List<Condition> additions=new ArrayList<>();
	
	private Map<String,FieldQuery> queryMap=new HashMap<>();
	
	public Map<String, FieldQuery> getQueryMap() {
		return queryMap;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonAnySetter
	public void addQueryMap(String name,FieldQuery query) {
		if(AdditionFieldType.verifyFieldName(name)) {
			query.setFieldName(name);
			if(query.verify()) {
				this.queryMap.put(name, query);
			}
		}
	}
	
	public Boolean getEnable() {
		return enable;
	}
	
	public Date getCreateTimeStart() {
		return createTimeStart;
	}
	
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}
	
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
	
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}
	
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	
	public QueryParam generQuery(){
		
		ConditionBuilder builder=ConditionBuilder.andCondition();
		if(StringUtils.isNotBlank(name)){
			builder.prefixLike("name",name);
		}
		if(StringUtils.isNotBlank(description)){
			builder.prefixLike("description",description);
		}
		
		if(enable!=null){
			builder.equal("status", enable? ThingStatusMonitor.MonitorStatus.enable: ThingStatusMonitor.MonitorStatus.disable);
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
		
		queryMap.values().forEach((q)->{
			
			builder.addSubClause(q.getKiiCondition());
		});
		return builder.getFinalQueryParam();
	}
}
