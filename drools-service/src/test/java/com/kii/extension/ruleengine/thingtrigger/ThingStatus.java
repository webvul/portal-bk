package com.kii.extension.ruleengine.thingtrigger;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class ThingStatus {
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ThingStatus that = (ThingStatus) o;
		return Objects.equal(thingID, that.thingID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(thingID);
	}

	private String thingID;

	private Map<String,Object> values=new HashMap<>();

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void addValue(String field,Object value){
		this.values.put(field,value);
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public String toString() {
		return "ThingStatus{" +
				"thingID='" + thingID + '\'' +
				", values=" + values +
				'}';
	}
}
