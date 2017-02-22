package com.kii.beehive.business.ruleengine.entitys;

import java.util.Map;

public class EngineBusinessObj {

	private String businessID;
	
	private Map<String,Object> state;
	
	
	public String getBusinessID() {
		return businessID;
	}
	
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	
	public Map<String, Object> getState() {
		return state;
	}
	
	public void setState(Map<String, Object> state) {
		this.state = state;
	}
}
