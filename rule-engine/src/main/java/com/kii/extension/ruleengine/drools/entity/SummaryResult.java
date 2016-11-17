package com.kii.extension.ruleengine.drools.entity;

public class SummaryResult implements  WithTrigger {

	private final String triggerID;


	private final String name;

	private final Object value;

//	private final String relID;

	private final String funName;



//	public SummaryResult(String triggerID,String summaryField,Number value){
//		this.triggerID=triggerID;
//		this.name=summaryField;
//		this.value=value;
//		this.relID=null;
//	}

	public SummaryResult(Summary summary,Number value){
		this.funName=summary.getFunName();
		this.triggerID=summary.getTriggerID();
		this.name=summary.getName();
		this.value=value;
	}

	public SummaryResult(String triggerID,String summaryField,Number value,String funName){
		this.triggerID=triggerID;
		this.name=summaryField;
		this.value=value;
		this.funName=funName;
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

	public String getFunName(){return funName;}



	@Override
	public String toString() {
		return "SummaryResult{" +
				"triggerID='" + triggerID + '\'' +
				", name=" + name +
				", values="+ value+
				'}';
	}


}
