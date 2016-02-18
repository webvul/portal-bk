package com.kii.extension.ruleengine.drools.entity;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;

public class Summary {

	private int triggerID;


	private String funName;

	private String fieldName;

	private String summaryField;

	private Set<String> things=new HashSet<>();

	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things = things;
	}

	public void addThing(String thingID){
		this.things.add(thingID);
	}

	public String getSummaryField() {
		return summaryField;
	}

	public void setSummaryField(String summaryField) {
		this.summaryField = summaryField;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Summary summary = (Summary) o;
		return triggerID == summary.triggerID &&
				Objects.equal(summaryField, summary.summaryField);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, summaryField);
	}
}
