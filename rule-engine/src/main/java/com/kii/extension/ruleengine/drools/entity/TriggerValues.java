package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

import com.kii.extension.ruleengine.ExpressTool;

public class TriggerValues implements  RuntimeEntry,WithTrigger,CanUpdate<TriggerValues> {



	private final String triggerID;
	
	
	private Map<String,Object> values=new HashMap<>();
	

	public TriggerValues(String triggerID){
		this.triggerID=triggerID;
	}
	
	
	public Map<String, Object> getValues() {
		return values;
	}
	
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	
	public Object getValue(String field){
		
		Object value= ExpressTool.getObjValue(this,field);
		
		return value;
	}

	public Object getNumValue(String field){
		
		return  ExpressTool.getNumValue(this,field);

	}
	
	@Override
	public String toString() {
		return "TriggerValues{" +
				"triggerID='" + triggerID + '\'' +
				", values=" + values +
				'}';
	}
	
	@Override
	public String getID() {
		return triggerID;
	}

	@Override
	public String getTriggerID() {
		return triggerID;
	}

	
	@Override
	public void update(TriggerValues update) {
		
		this.values.putAll(update.values);
	}
	
	
	public void updateValue(String key,Object value){
		this.values.put(key,value);
	}
}
