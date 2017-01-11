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
		this.businessObj.setBusinessType(BusinessObjType.Global);
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
	
//	public static class Param {
//
//		private String name;
//
//		private String valueExpress;
//
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//
//		public String getValueExpress() {
//			return valueExpress;
//		}
//
//		public void setValueExpress(String valueExpress) {
//			this.valueExpress = valueExpress;
//		}
//	}
}
