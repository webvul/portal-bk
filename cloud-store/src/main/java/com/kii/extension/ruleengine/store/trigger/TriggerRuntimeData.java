package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class TriggerRuntimeData extends KiiEntity{

	private  String triggerGroupID;


	private Map<String,Object> data=new HashMap<>();

	public String getTriggerGroupID() {
		return triggerGroupID;
	}

	public void setTriggerGroupID(String triggerGroupID) {
		this.triggerGroupID = triggerGroupID;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
