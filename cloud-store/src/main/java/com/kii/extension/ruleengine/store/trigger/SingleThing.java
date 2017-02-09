package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SingleThing {
	
	private Long thingID;
	
	private String userID;
	
	private String triggerGroupName;
	
	private String businessName;
	
	private String businessID;
	
	private BusinessObjType businessType;
	
	private BusinessObjType type;
	
	public BusinessObjType getBusinessType() {
		return type;
	}
	
	public void setBusinessType(BusinessObjType type) {
		this.type = type;
	}
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	
	public String getUserID(){
		return userID;
	}

	
	public void setUserID(String userID) {
		this.userID = userID;
		businessType= BusinessObjType.User;
	}
	
	
	public String getTriggerGroupName(){
		return triggerGroupName;
	}

	
	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
		businessType= BusinessObjType.TriggerGroup;
		
	}
	
	public String getBusinessID() {
		return businessID;
	}
	
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	
	@JsonIgnore
	public BusinessDataObject getBusinessObj(){
		
		String id=null;
		
		if(businessType==null){
			return new BusinessDataObject(businessID,businessName,type);
		}
		
		switch(businessType){
			
			case Thing:id=String.valueOf(thingID);break;
			case User:id=userID;break;
			case TriggerGroup:id=triggerGroupName;break;
		}
		
		return new BusinessDataObject(id,businessName,businessType);
	}
	
	
	public Long getThingID(){
		return this.thingID;
	}
	
	public void setThingID(Long thingID) {
		this.thingID =thingID;
		businessType= BusinessObjType.Thing;
	}
	
}
