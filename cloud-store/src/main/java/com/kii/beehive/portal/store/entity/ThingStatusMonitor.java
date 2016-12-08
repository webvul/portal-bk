package com.kii.beehive.portal.store.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.sdk.entity.KiiEntity;


public class ThingStatusMonitor extends KiiEntity {

	private String name;
	
	private String  description;
	
	private Map<String,Boolean> vendorThingIDs=new HashMap<>();
	
	private Condition condition;

	private String express;

	private String relationTriggerID;

	private Long creator;
	
	private List<Long> noticeList=new ArrayList<>();
	
	private MonitorStatus status;
	
	public void updateThingIDs(Collection<String> vendorThingIDList) {
		
		Set<String> ids=new HashSet<>(vendorThingIDList);
		
		for(String id:ids){
			vendorThingIDs.putIfAbsent(id,null);
		}
	}
	
	
	public enum MonitorStatus{
		enable,disable,deleted;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}


	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getRelationTriggerID() {
		return relationTriggerID;
	}

	public void setRelationTriggerID(String relationTriggerID) {
		this.relationTriggerID = relationTriggerID;
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}
	
	public Map<String, Boolean> getVendorThingIDs() {
		return vendorThingIDs;
	}
	
	public void setVendorThingIDs(Map<String, Boolean> vendorThingIDs) {
		this.vendorThingIDs = vendorThingIDs;
	}
	
	public void setVendorThingIDList(Collection<String> idList){
		
		for(String id:idList){
			vendorThingIDs.put(id,true);
		}
	}
	
	
	@JsonIgnore
	public Collection<String> getVendorThingIDList(){
		
		return vendorThingIDs.entrySet().stream().filter(entry->entry.getValue().booleanValue()).map(entry->entry.getKey()).collect(Collectors.toSet());
	}
	
	public MonitorStatus getStatus() {
		return status;
	}
	
	public void setStatus(MonitorStatus status) {
		this.status = status;
	}
	
	public List<Long> getNoticeList() {
		return noticeList;
	}
	
	public void setNoticeList(List<Long> noticeList) {
		this.noticeList = noticeList;
	}
}
