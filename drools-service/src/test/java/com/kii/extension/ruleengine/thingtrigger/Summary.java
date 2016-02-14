package com.kii.extension.ruleengine.thingtrigger;

public class Summary {

	private int triggerID;


	private String funName;

	private String fieldName;

	private String summaryField;



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
}
