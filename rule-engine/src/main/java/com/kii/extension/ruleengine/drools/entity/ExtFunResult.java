package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;

public class ExtFunResult implements  RuntimeEntry,WithTrigger {


	private final String triggerID;


	private final String name;

	private final Object value;

	public ExtFunResult(String triggerID,String name,ThingStatusInRule status){
		this.triggerID=triggerID;
		this.name=name;
		this.value=status.getValues();
	}



	public ExtFunResult(String triggerID){
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
//
//	public Map<String, Object> getValue() {
//		return value;
//	}


	@Override
	public String toString() {
		return "ThingResult{" +
				"triggerID='" + triggerID + '\'' +
				", name='" + name + '\'' +
				", value=" + value +
				'}';
	}

	@Override
	public String getID() {
		return triggerID+":thing:"+name;
	}

}
