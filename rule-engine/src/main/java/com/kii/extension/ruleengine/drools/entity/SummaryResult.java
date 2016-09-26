package com.kii.extension.ruleengine.drools.entity;

public class SummaryResult implements  WithTrigger {

	private final String triggerID;


	private final String name;

	private final Object value;



	public SummaryResult(String triggerID,String summaryField,Number value){
		this.triggerID=triggerID;
		this.name=summaryField;
		this.value=value;
	}


	public String getTriggerID() {
		return triggerID;
	}

	public Object  getValue() {
		return value;
	}

	public String getName(){
		return name;
	}



	@Override
	public String toString() {
		return "SummaryResult{" +
				"triggerID='" + triggerID + '\'' +
				", name=" + name +
				", values="+ value+
				'}';
	}


}
