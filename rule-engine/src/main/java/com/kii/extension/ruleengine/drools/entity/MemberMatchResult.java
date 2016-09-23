package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

public class MemberMatchResult implements  WithTrigger{


	private String thingID;

	private String triggerID;


	private String name;

	private Map<String,Object> values=new HashMap<>();

	public Object getNumValue(String field){
		Object val=values.get(field);
		if(val==null){
			return 0;
		}
		if(val instanceof String) {
			try {
				return Double.parseDouble(String.valueOf(val));
			}catch(NumberFormatException e){
				return 0;
			}
		}
		return val;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MemberMatchResult(Summary group, ThingStatusInRule status){

		this.triggerID=group.getTriggerID();
		this.name=group.getName();
		this.values=status.getValues();
		this.thingID=status.getThingID();

	}

	public MemberMatchResult(String triggerID, String name,String thingID) {
		this.thingID = thingID;
		this.triggerID = triggerID;
		this.name = name;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}


	@Override
	public String toString() {
		return "MemberMatchResult{" +
				"thingID='" + thingID + '\'' +
				", triggerID=" + triggerID +
				'}';
	}

//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		MemberMatchResult that = (MemberMatchResult) o;
//		return Objects.equals(thingID, that.thingID) &&
//				Objects.equals(triggerID, that.triggerID) &&
//				Objects.equals(name, that.name);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(thingID, triggerID, name);
//	}
}
