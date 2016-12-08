package com.kii.beehive.business.entity;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.kii.beehive.portal.jdbc.entity.NoticeActionType;
import com.kii.beehive.portal.store.entity.ThingStatusMonitor;

public class ThingStatusNoticeEntry {
	
	
	private Map<String,Object> currStatus;
	
	private String currThing;
	
	private NoticeActionType.ThingMonitorType actionType;
	
	private Set<String> currMatchers;
	
	private ThingStatusMonitor monitor;
	
	public Map<String, Object> getCurrStatus() {
		return currStatus;
	}
	
	public void setCurrStatus(Map<String, Object> currStatus) {
		this.currStatus = currStatus;
	}
	
	public String getCurrThing() {
		return currThing;
	}
	
	public void setCurrThing(String currThing) {
		this.currThing = currThing;
	}
	
	public NoticeActionType.ThingMonitorType getActionType() {
		return actionType;
	}
	
	public void setActionType(NoticeActionType.ThingMonitorType actionType) {
		this.actionType = actionType;
	}
	
	public Set<String> getCurrMatchers() {
		return currMatchers;
	}
	
	public void setCurrMatchers(Set<String> currMatchers) {
		this.currMatchers = currMatchers;
	}
	
	public ThingStatusMonitor getMonitor() {
		return monitor;
	}
	
	public void setMonitor(ThingStatusMonitor monitor) {
		
		this.monitor = new ThingStatusMonitor();
		
		BeanUtils.copyProperties(monitor,this.monitor,"relationTriggerID","noticeList","status");
		
	}
}
