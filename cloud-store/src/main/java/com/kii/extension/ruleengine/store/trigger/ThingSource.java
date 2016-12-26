package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingSource implements SourceElement {
	
	private String businessName;
	
	private String businessID;
	
	private BusinessObjType businessType= BusinessObjType.Thing;
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	
	public String getUserID(){
		return businessID;
	}
	
	public void setUserID(String userID) {
		this.businessID = userID;
		businessType= BusinessObjType.User;
	}
	
	
	public String getTriggerGroupName(){
		return businessID;
	}
	
	public void setTriggerGroupName(String triggerGroupName) {
		this.businessID = triggerGroupName;
		businessType= BusinessObjType.TriggerGroup;
		
	}
	
	public String getBusinessID() {
		return businessID;
	}
	
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	
	public BusinessObjType getBusinessType() {
		return businessType;
	}
	
	public void setBusinessType(BusinessObjType businessType) {
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

	public String getThingID(){
		return this.businessID;
	}

	public void setThingID(Long thingID) {
		this.businessID = String.valueOf(thingID);
		businessType= BusinessObjType.Thing;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
	
	
	@JsonIgnore
	public BusinessDataObject getBusinessObj(){
		
		return new BusinessDataObject(businessID,businessName,businessType);
	}
}
