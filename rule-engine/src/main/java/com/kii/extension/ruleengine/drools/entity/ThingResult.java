package com.kii.extension.ruleengine.drools.entity;

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
}
