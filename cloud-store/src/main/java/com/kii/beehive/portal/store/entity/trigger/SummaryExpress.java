package com.kii.beehive.portal.store.entity.trigger;

public class SummaryExpress {

	private String stateName;

	private SummaryFunctionType function;

	private String sumField;

	private String countField;

	public String getSumField() {
		return sumField;
	}

	public void setSumField(String sumField) {
		this.sumField = sumField;
	}

	public String getCountField() {
		return countField;
	}

	public void setCountField(String countField) {
		this.countField = countField;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public SummaryFunctionType getFunction() {
		return function;
	}

	public void setFunction(SummaryFunctionType function) {
		this.function = function;
	}
}
