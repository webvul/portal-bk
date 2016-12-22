package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingSource implements SourceElement {

	private Long thingID;
	
	private String userID;
	
	private String triggerID;
	
	private String businessID;
	
	private String businessType=BusinessDataObject.BusinessObjType.Thing.name();
	
	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
		businessType=BusinessDataObject.BusinessObjType.User.name();
	}
	
	public String getTriggerID() {
		return triggerID;
	}
	
	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
		businessType=BusinessDataObject.BusinessObjType.Trigger.name();
		
	}
	
	public String getBusinessID() {
		return businessID;
	}
	
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	
	public String getBusinessType() {
		return businessType;
	}
	
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	
	@JsonUnwrapped
	private Express express=new Express();


	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public Long getThingID() {
		return thingID;
	}

	public void setThingID(Long thingID) {
		this.thingID = thingID;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
}
