package com.kii.beehive.portal.store.entity.trigger.task;


import java.util.Map;

import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;

public class SettingParameterResponse extends TriggerResult {
	@Override
	public String getType() {
		return "settingParameter";
	}
	
	private BusinessDataObject  object;
	
	private Map<String,Object> inputParam;
	
	
	public BusinessDataObject getSettingObject() {
		return object;
	}
	
	public void setSettingObject(BusinessDataObject object) {
		this.object = object;
	}
	
	public Map<String, Object> getInputParam() {
		return inputParam;
	}
	
	public void setInputParam(Map<String, Object> inputParam) {
		this.inputParam = inputParam;
	}
}
