package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kii.extension.ruleengine.ExpressTool;

public class MultiplesValueMap implements RuntimeEntry,WithTrigger,WithHistory{

	private String triggerID;

	private Map<String,Object> valueMap=new HashMap<>();
	
	private Map<String,Object> previousMap=new HashMap<>();
	
	private AtomicBoolean sign=new AtomicBoolean(false);
	
	public boolean copyToHistory(){
//		if(sign.get()){
//			return true;
//		}
		
		previousMap.clear();
		
		valueMap.forEach((k,v)->{
			
			if(v instanceof Set){
				
				previousMap.put(k,new HashSet<>((Set)v));
			}else if(v instanceof Map){
				previousMap.put(k,new HashMap<>((Map)v));
			}else{
				previousMap.put(k,v);
			}
			
		});
//		previousMap.putAll(valueMap);
		
//		sign.set(true);
//		return sign.get();
		return true;
	}
	
	public void setSummaryValue(SummaryResult result){

		String name=result.getName();
		Object value=result.getValue();
		if(value instanceof Long  || value instanceof Integer){
			value=((Number)value).doubleValue();
		}
		valueMap.put(name,value);
//		sign.set(false);
	}

	public void setThingValue(ThingResult result){

		String name=result.getName();

		if(name.equals("NONE")){
			return;
		}

		Map<String,Object> map=result.getValue();
		
		valueMap.put(name,map);
//		sign.set(false);
//		map.forEach((k,v)->{
//			valueMap.put(name+"."+k,v);
//		});
	}

	public Map<String,Object> getValues(){
		return valueMap;
	}
	
	public Map<String,Object> getPrevious(){
		return previousMap;
	}
	
	
	public Object getValue(String key){
		
		Object value= ExpressTool.getObjValue(this,key);

		return value;
	}
	
	
	public Set<?> getSetValue(String key){
		
		Set<?> value= ExpressTool.getValue(this,key,Set.class);
		
		return value==null?new HashSet<>():(Set)value;
	}
	
	public Map<String,?> getMapValue(String key){
		
		Map<String,?> value= ExpressTool.getValue(this,key,Map.class);
		
		return value==null?new HashMap<>():(HashMap)value;
	}
	
	
	public Number getNumValue(String key){
		
		Number value= ExpressTool.getNumValue(this,key);
		return value;
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
