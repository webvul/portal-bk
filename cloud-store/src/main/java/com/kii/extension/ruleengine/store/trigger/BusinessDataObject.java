package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.KiiEntity;

public class BusinessDataObject extends KiiEntity {

	private   String  businessObjID;
	
	private BusinessObjType businessType=BusinessObjType.Trigger;

	private   Map<String,Object> data=new HashMap<>();
	
	@JsonIgnore
	public String getFullObjID(){
		return businessType.name()+":"+businessObjID;
	}
	
	public BusinessObjType getBusinessType() {
		return businessType;
	}
	
	public void setBusinessType(BusinessObjType businessType) {
		this.businessType = businessType;
	}
	
	public String getBusinessObjID() {
		return businessObjID;
	}

	public void setBusinessObjID(String businessObjID) {
		this.businessObjID = businessObjID;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public enum BusinessObjType{
		User,Trigger,Thing;
	}
}
