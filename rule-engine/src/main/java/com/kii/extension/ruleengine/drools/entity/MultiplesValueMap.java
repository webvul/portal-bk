package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

public class MultiplesValueMap {

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

		if(!valueMap.containsKey(key)){
			return 0.0d;
		}
		Object obj=  valueMap.get(key);

		if(obj instanceof Long ||obj instanceof Integer){
			return ((Number)obj).doubleValue();
		}

		return obj;

	}


	public Object getNumValue(String key){
		Object val= valueMap.get(key);
		if(val==null){
			return 0.0d;
		}else if(val instanceof  String) {
			return Double.parseDouble((String)val);
		}else{
			return val;
		}
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

}
