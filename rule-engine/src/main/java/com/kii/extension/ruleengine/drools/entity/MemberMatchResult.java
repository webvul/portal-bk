package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class MemberMatchResult {


	private String thingID;

	private String triggerID;


	private String name;

	private Map<String,Object> values=new HashMap<>();

	public Object getSafeValue(String field){
		return
				this.values.getOrDefault(field,0);
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

	public MemberMatchResult(Group group,ThingStatusInRule status){

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemberMatchResult that = (MemberMatchResult) o;
		return Objects.equal(triggerID,that.triggerID) &&
				Objects.equal(thingID, that.thingID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(thingID, triggerID);
	}

	@Override
	public String toString() {
		return "MemberMatchResult{" +
				"thingID='" + thingID + '\'' +
				", triggerID=" + triggerID +
				'}';
	}
}
