package com.kii.extension.ruleengine.drools.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TriggerValues implements  RuntimeEntry,WithTrigger {



	private String triggerID;

	public TriggerValues(String triggerID){
		this.triggerID=triggerID;
	}

	private Map<String,ExternalValues> externalValuesMap=new ConcurrentHashMap<>();


	public Map<String, ExternalValues> getExternalValuesMap() {
		return externalValuesMap;
	}

	public void setExternalValuesMap(Map<String, ExternalValues> externalValuesMap) {
		this.externalValuesMap = externalValuesMap;
	}

	public ExternalValues getEntity(String name){
		return externalValuesMap.getOrDefault(name,new ExternalValues(name));
	}


	public void updateEntity(ExternalValues values){
		putEntity(values.getName(),values);
	}

	private void putEntity(String name,ExternalValues  values){

		externalValuesMap.merge(name,values,(oldV,newV)-> {
			oldV.merge(newV);
			return oldV;
		});

	}

	public Object getValue(String fullPath){

		int idx=fullPath.indexOf(".");

		if(idx==-1){
			return null;
		}

		String key= fullPath.substring(0,idx);
		String name=fullPath.substring(idx+1);

		return getEntity(key).getValue(name);
	}

	public Object getNumValue(String fullPath){

		int idx=fullPath.indexOf(".");

		if(idx==-1){
			return null;
		}

		String key= fullPath.substring(0,idx);
		String name=fullPath.substring(idx+1);

		Object val= getEntity(key).getValue(name);
		if(val==null){
			return 0;
		}
		return val;

	}


	@Override
	public String toString() {
		return "ExternalCollect{" +
				"externalValuesMap=" + externalValuesMap +
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

	public void setTriggerID(String triggerID){
		this.triggerID=triggerID;
	}
}
