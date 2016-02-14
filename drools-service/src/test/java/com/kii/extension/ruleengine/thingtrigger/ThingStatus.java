package com.kii.extension.ruleengine.thingtrigger;

import java.util.HashMap;
import java.util.Map;

public class ThingStatus {

	private String thingID;


	private Map<String,Object> values=new HashMap<>();

	private int status;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
