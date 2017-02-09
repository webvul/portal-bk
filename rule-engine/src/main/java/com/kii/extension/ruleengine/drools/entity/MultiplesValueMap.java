package com.kii.extension.ruleengine.drools.entity;

import java.util.Collections;
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

		return true;
	}
	
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
		
		valueMap.put(name,map);

	}

	public Map<String,Object> getValues(){
		return valueMap;
	}
	
	public Map<String,Object> getPrevious(){
		return previousMap;
	}
	
	
	public Object getValue(String key){
		
//		boolean isPrevious=false;
//		if(key.startsWith("previous")){
//			key= StringUtils.substring(key,9);
//			isPrevious=true;
//		}
		
		Object value= ExpressTool.getObjValue(this,key);

		return value;
	}
	
	
//	private Object getValueByKey(String key){
//
//		if(key.startsWith("previous")){
//			String subKey= StringUtils.substring(key,9);
//			return previousMap.get(subKey);
//		}else{
//			return valueMap.get(key);
//		}
//	}
	
	public Set<?> getSetValue(String key){
		
//		Object obj=getValueByKey(key);
//		if(obj!=null){
//			return (Set<?>) obj;
//		}
		
		Set<?> value= ExpressTool.getValue(this,key,Set.class);
		
		return value==null? Collections.EMPTY_SET:(Set)value;
	}
	
	public Map<String,?> getMapValue(String key){
		
//		Object obj=getValueByKey(key);
//		if(obj!=null){
//			return (Map<String, ?>) obj;
//		}
//
		Map<String,?> value= ExpressTool.getValue(this,key,Map.class);
		
		return value==null?Collections.EMPTY_MAP:(HashMap)value;
	}
	
	
	public Number getNumValue(String key){
		
//		Object obj=getValueByKey(key);
//		if(obj!=null){
//			return (Number) obj;
//		}
		
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
