package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class SummaryResult {

	private String triggerID;

	private String summaryField;

	private Number  value;

	public SummaryResult(){

	}

	public SummaryResult(String triggerID,String summaryField,Number value){
		this.triggerID=triggerID;
		this.value=value;
		this.summaryField=summaryField;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getSummaryField() {
		return summaryField;
	}

	public void setSummaryField(String summaryField) {
		this.summaryField = summaryField;
	}

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SummaryResult that = (SummaryResult) o;
		return Objects.equal(triggerID,that.triggerID) &&
				Objects.equal(summaryField, that.summaryField);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, summaryField);
	}
}
