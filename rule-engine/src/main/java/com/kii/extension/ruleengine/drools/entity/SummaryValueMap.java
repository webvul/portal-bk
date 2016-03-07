package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class SummaryValueMap {

	private String triggerID;

	private Map<String,Number> numberMap=new HashMap<>();

	public Map<String, Number> getValues() {
		return numberMap;
	}

	public Number getNumber(String field){
		return numberMap.get(field);
	}

	public void setNumber(String field,Number value){
		numberMap.put(field,value);
	}

	public void setValues(Map<String, Number> numberMap) {
		this.numberMap = numberMap;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SummaryValueMap that = (SummaryValueMap) o;
		return Objects.equal(triggerID,that.triggerID) ;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}

	@Override
	public String toString() {
		return "SummaryValueMap{" +
				"triggerID='" + triggerID + '\'' +
				", numberMap=" + numberMap +
				'}';
	}
}
