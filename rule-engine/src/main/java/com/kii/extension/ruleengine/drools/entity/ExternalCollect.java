package com.kii.extension.ruleengine.drools.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExternalCollect implements Serializable{

	private Map<String,ExternalValues> externalValuesMap=new ConcurrentHashMap<>();


	public Map<String, ExternalValues> getExternalValuesMap() {
		return externalValuesMap;
	}

	public void setExternalValuesMap(Map<String, ExternalValues> externalValuesMap) {
		this.externalValuesMap = externalValuesMap;
	}

	public ExternalValues getEntity(String name){
		return externalValuesMap.get(name);
	}

	public void putEntity(String name,ExternalValues  values){

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

		return externalValuesMap.get(key).getValue(name);
	}

	public Object getNumValue(String fullPath){

		int idx=fullPath.indexOf(".");

		if(idx==-1){
			return null;
		}

		String key= fullPath.substring(0,idx);
		String name=fullPath.substring(idx+1);

		Object val= externalValuesMap.get(key).getValue(name);
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
}
