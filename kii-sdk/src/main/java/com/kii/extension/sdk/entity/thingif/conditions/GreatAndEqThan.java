package com.kii.extension.sdk.entity.thingif.conditions;

public class GreatAndEqThan extends RangeCondition{


	private Object upperLimit;

	private boolean upperIncluded;


	@Override
	public ConditionType getType(){
		return ConditionType.eq_gt;
	}

	public Object getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(Object upperLimit) {
		this.upperLimit = upperLimit;
	}

	public boolean isUpperIncluded() {
		return upperIncluded;
	}

	public void setUpperIncluded(boolean upperIncluded) {
		this.upperIncluded = upperIncluded;
	}

}
