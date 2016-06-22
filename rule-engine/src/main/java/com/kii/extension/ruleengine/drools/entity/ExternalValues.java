package com.kii.extension.ruleengine.drools.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExternalValues implements Serializable {


	private Map<String,Object> values=new HashMap<>();

	private final String name;


	public ExternalValues(String name){
		this.name=name;
	}
	public String getName() {
		return name;
	}


	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void addValue(String key,Object value){
		values.put(key,value);
	}

	public ExternalValues merge(ExternalValues newV) {

		values.putAll(newV.getValues());

		return this;
	}
	
	public Object getValue(String name) {

		return values.get(name);
	}
}
