package com.kii.extension.ruleengine.drools.entity;

public abstract class TriggerData {


	private String triggerID;

	private String name;


	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
