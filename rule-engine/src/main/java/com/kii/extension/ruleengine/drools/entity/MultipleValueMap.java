package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class MultipleValueMap {

	private String triggerID;

	private Map<String,Boolean> booleanMap=new HashMap<>();


	public boolean getValue(String name){
		return booleanMap.getOrDefault(name,false);
	}

	public void setValue(String name){
		booleanMap.put(name,true);
	}


	public Map<String, Boolean> getValues() {
		return booleanMap;
	}

	public void setValues(Map<String, Boolean> booleanMap) {
		this.booleanMap = booleanMap;
	}

	public void clear(){
		booleanMap.clear();
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
				", booleanMap=" + booleanMap +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultipleValueMap that = (MultipleValueMap) o;
		return Objects.equal(triggerID, that.triggerID) &&
				Objects.equal(booleanMap, that.booleanMap);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, booleanMap);
	}
}
