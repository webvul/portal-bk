package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class MultipleValueMap {

	private String triggerID;

	private Map<String,Object> valueMap=new HashMap<>();

	public void setSummaryMap(SummaryValueMap map){

		valueMap.putAll(map.getValues());
	}

	public void setUnitValue(String  name,Object value){
		valueMap.put(name,value);
	}

	public Map<String,Object> getValues(){
		return valueMap;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultipleValueMap that = (MultipleValueMap) o;
		return Objects.equal(triggerID, that.triggerID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}
}
