package com.kii.extension.ruleengine.drools.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThingStatusInRule implements RuntimeEntry,CanUpdate<ThingStatusInRule>{

	private final String thingID;

	private Date createAt;

	private Map<String,Object> values=new HashMap<>();

	public ThingStatusInRule(String thingID){
		this.thingID=thingID;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void addValue(String field,Object value){
		this.values.put(field,value);
	}



	public Object getNumValue(String field){
		Object value = this.values.get(field);
		return value == null ? 0 : value;
	}

	public Object getValue(String field){
		Object value = this.values.get(field);
		if(value==null){
			return null;
		}
		return value;
	}

	public String getThingID() {
		return thingID;
	}


	@Override
	public String toString() {
		return "ThingStatus{" +
				"thingID='" + thingID + '\'' +
				", values=" + values +
				'}';
	}

	@Override
	public String getID() {
		return thingID;
	}

	@Override
	public void update(ThingStatusInRule update) {

		Map<String,Object>  vals=update.getValues();
		if(vals!=null){
			this.values.putAll(vals);
		}

	}
}
