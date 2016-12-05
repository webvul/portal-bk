package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kii.extension.ruleengine.ExpressTool;

public class MultiplesValueMap implements RuntimeEntry,WithTrigger{

	private String triggerID;

	private Map<String,Object> valueMap=new HashMap<>();

	public void setSummaryValue(SummaryResult result){

		String name=result.getName();
		Object value=result.getValue();
		if(value instanceof Long  || value instanceof Integer){
			value=((Number)value).doubleValue();
		}
		valueMap.put(name,value);

	}

	public void setThingValue(ThingResult result){

		String name=result.getName();

		if(name.equals("NONE")){
			return;
		}

		Map<String,Object> map=result.getValue();
		map.forEach((k,v)->{
			valueMap.put(name+"."+k,v);
		});
	}

	public Map<String,Object> getValues(){
		return valueMap;
	}

	public Object getValue(String key){
		
		Object value= ExpressTool.getValue(this,key);

		return value;
	}
	
	public Set<?> getSetValue(String key){
		
		Object value= ExpressTool.getValue(this,key);
		
		return value==null?new HashSet<>():(Set)value;
	}


	public Object getNumValue(String key){
		
		Object value= ExpressTool.getValue(this,key);
		return value == null ? 0 : value;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	@Override
	public String toString() {
		return "MultipleValueMap{" +
				"triggerID='" + triggerID + '\'' +
				", valueMap=" + valueMap +
				'}';
	}

	@Override
	public String getID() {
		return triggerID;
	}
}
