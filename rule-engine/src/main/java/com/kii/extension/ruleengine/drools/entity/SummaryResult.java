package com.kii.extension.ruleengine.drools.entity;

public class SummaryResult implements  WithTrigger {

	private final String triggerID;


	private final String name;

	private final Object value;

	private final String relID;



	public SummaryResult(String triggerID,String summaryField,Number value){
		this.triggerID=triggerID;
		this.name=summaryField;
		this.value=value;
		this.relID=null;
	}


	public SummaryResult(String triggerID,String summaryField,Number value,String relID){
		this.triggerID=triggerID;
		this.name=summaryField;
		this.value=value;
		this.relID=relID;
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

	public String getRelID() {
		return relID;
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
