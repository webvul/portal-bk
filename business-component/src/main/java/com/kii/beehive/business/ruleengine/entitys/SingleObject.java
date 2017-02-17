package com.kii.beehive.business.ruleengine.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

public class SingleObject {

	
	private String businessName;
	
	private String businessID;
	
	private BusinessObjType type=BusinessObjType.Business;
	
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
	
	public String getBusinessID() {
		return businessID;
	}
	
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	
	@JsonIgnore
	public BusinessDataObject getBusinessObj(){

		return new BusinessDataObject(businessID,businessName,type);
	}
	
}
