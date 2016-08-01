package com.kii.extension.ruleengine.drools.entity;

public class SummaryResult {

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

//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		SummaryResult that = (SummaryResult) o;
//		return Objects.equals(triggerID, that.triggerID) &&
//				Objects.equals(name, that.name);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(triggerID, name);
//	}
}
