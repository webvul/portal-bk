package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExternalValues {


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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExternalValues that = (ExternalValues) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
	public ExternalValues merge(ExternalValues newV) {

		values.putAll(newV.getValues());

		return this;
	}
	
	public Object getValue(String name) {

		return values.get(name);
	}
}
