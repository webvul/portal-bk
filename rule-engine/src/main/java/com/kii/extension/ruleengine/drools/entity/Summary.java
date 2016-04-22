package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class Summary extends ThingCol implements TriggerData{

	private String triggerID;

	private String funName;

	private String fieldName;

//	private String summaryField;




//	private Set<String> things=new HashSet<>();

//	public Set<String> getThings() {
//		return things;
//	}
//
//	public void setThings(Set<String> things) {
//		this.things = things;
//	}
//
//	public void addThing(String thingID){
//		this.things.add(thingID);
//	}

//	public String getSummaryField() {
//		return summaryField;
//	}
//
//	public void setSummaryField(String summaryField) {
//		this.summaryField = summaryField;
//	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
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
		return Objects.equal(triggerID,summary.triggerID)  &&
				Objects.equal(super.getName(), summary.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, super.getName());
	}

	@Override
	public String toString() {
		return "Summary{" +
				"triggerID='" + triggerID + '\'' +
				", funName='" + funName + '\'' +
				", fieldName='" + fieldName + '\'' +
				", summaryField='" + name + '\'' +
				", things=" + super.getThings() +
				'}';
	}
}
