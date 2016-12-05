package com.kii.beehive.portal.store.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.sdk.entity.KiiEntity;


public class ThingStatusMonitor extends KiiEntity {

	private String name;
	
//	private List<Long> thingIDs;
	
	private Map<String,Boolean> vendorThingIDs=new HashMap<>();
	
	private Condition condition;

	private String express;

	private String relationTriggerID;

	private Long creator;
	
	private List<Long> noticeList;
	
	private MonitorStatus status;
	
	public enum MonitorStatus{
		enable,disable,deleted;
	}

	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
//	public List<Long> getThingIDs() {
//		return thingIDs;
//	}
//
//	public void setThingIDs(List<Long> thingIDs) {
//		this.thingIDs = thingIDs;
//	}

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
		
		return vendorThingIDs.keySet();
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
