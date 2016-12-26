package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final Pattern pattern=Pattern.compile("(([^\\:\\-]+)(\\-([^:]+))?)\\:([\\S]+)");
	
	public static BusinessDataObject getInstance(String fullBuinessID){
		
		Matcher match=pattern.matcher(fullBuinessID);
		
		if(match.find()){
			BusinessDataObject obj=new BusinessDataObject();
			obj.businessType=BusinessObjType.valueOf(match.group(2));
			obj.businessName=match.group(4);
			obj.businessObjID=match.group(5);
			
			return obj;
			
		}else{
			return null;
		}
	}
	
	@JsonIgnore
	public String getFullID(){
//		String name=;
//		if(StringUtils.isBlank(name)){
//			name="comm";
//		}
		return businessType.getFullID(businessObjID,businessName);
	}
	
	public String getBusinessName() {
		return businessName;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
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
