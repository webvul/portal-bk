package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.KiiEntity;

public class BusinessDataObject extends KiiEntity {

	private   String  businessObjID;
	
	private String businessName;
	
	private BusinessObjType businessType=BusinessObjType.TriggerGroup;

	private   Map<String,Object> data=new HashMap<>();
	
	
	public BusinessDataObject(){
		
	}
	
	public BusinessDataObject(String businessObjID,String businessName,BusinessObjType businessType){
		
		setBusinessName(businessName);
		setBusinessObjID(businessObjID);
		setBusinessType(businessType);
		
	}
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	@JsonIgnore
	public String getFullObjID(){
		String name=businessName;
		if(StringUtils.isBlank(name)){
			name="comm";
		}
		return businessType.name()+""+name+"-"+businessObjID;
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
	
}
