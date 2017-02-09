package com.kii.extension.ruleengine.store.trigger.task;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;

public class SettingTriggerGroupParameter extends ExecuteTarget {




	@Override
	public TargetType getType() {
		return TargetType.SettingParameter;
	}



	private BusinessDataObject businessObj=new BusinessDataObject();
	
	private Map<String,String> paramMap=new HashMap<>();
	
	private Map<String,Object> valueMap=new HashMap<>();
	
	public Map<String, Object> getValueMap() {
		return valueMap;
	}
	
	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}
	
	public String getGroupName() {
		return businessObj.getBusinessObjID();
	}
	
	public void setGroupName(String groupName) {
		this.businessObj.setBusinessObjID(groupName);
		this.businessObj.setBusinessType(BusinessObjType.TriggerGroup);
		
	}
	
	public String getExtensionName() {
		return businessObj.getBusinessObjID();
	}
	
	public void setExtensionName(String parameterName) {
		this.businessObj.setBusinessObjID(parameterName);
		this.businessObj.setBusinessType(BusinessObjType.Context);
	}
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	@JsonIgnore
	public BusinessDataObject getBusinessObj() {
		return businessObj;
	}
	
	public void setBusinessObj(BusinessDataObject businessObj) {
		this.businessObj = businessObj;
	}
	

}
