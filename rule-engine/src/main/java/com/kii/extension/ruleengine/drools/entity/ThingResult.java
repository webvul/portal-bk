package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;

public class ThingResult {


	private final String triggerID;


	private final String name;

	private final Map<String,Object> value;

	public ThingResult(String triggerID,String name,ThingStatusInRule status){
		this.triggerID=triggerID;
		this.name=name;
		this.value=status.getValues();
	}



	public ThingResult(String triggerID){
		this.triggerID=triggerID;
		this.name="NONE";
		this.value=new HashMap<>();
	}


	public String getTriggerID() {
		return triggerID;
	}

	public String getName() {
		return name;
	}

	public Map<String, Object> getValue() {
		return value;
	}


	@Override
	public String toString() {
		return "ThingResult{" +
				"triggerID='" + triggerID + '\'' +
				", name='" + name + '\'' +
				", value=" + value +
				'}';
	}

//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		ThingResult that = (ThingResult) o;
//		return Objects.equals(triggerID, that.triggerID) &&
//				Objects.equals(name, that.name);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(triggerID, name);
//	}
}
