package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.sdk.entity.KiiEntity;


public class ThingStatusMonitor extends KiiEntity {

	private String name;
	
	private Set<Long> things;

	private Condition condition;

	private String express;

	private String relationTriggerID;

	private String creator;
	
	private Set<String> relationFields=new HashSet<>();
	
	private List<Long> noticeList;
	
	private MonitorStatus status;
	
	public enum MonitorStatus{
		enable,disable,deleted;
	}
	
	public Set<String> getRelationFields() {
		return relationFields;
	}
	
	public void setRelationFields(Set<String> relationFields) {
		this.relationFields = relationFields;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Long> getThings() {
		return things;
	}

	public void setThings(Set<Long> things) {
		this.things = things;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
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
