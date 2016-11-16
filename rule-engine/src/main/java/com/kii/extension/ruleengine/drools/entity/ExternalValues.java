package com.kii.extension.ruleengine.drools.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
	
	public Object getValue(String field) {


		int idx=field.indexOf(".");
		if(idx==-1){
			idx=field.length();
		}
		String prefix= StringUtils.substring(field,0,idx);
		String fullField="values['"+prefix+"']"+StringUtils.substring(field,idx);


		return values.get(name);
	}
}
