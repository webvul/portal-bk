package com.kii.beehive.business.entity;

import java.util.Map;

public class MonitorResult {
	
	
	private Map<String,Object> status;
	
	private String thing;
	
	private Long thingInThID;
	
	
	
	public Map<String, Object> getStatus() {
		return status;
	}
	
	public void setStatus(Map<String, Object> status) {
		this.status = status;
	}
	
	public String getThing() {
		return thing;
	}
	
	public void setThing(String thing) {
		this.thing = thing;
	}
	
	public Long getThingInThID() {
		return thingInThID;
	}
	
	public void setThingInThID(Long thingInThID) {
		this.thingInThID = thingInThID;
	}
}
