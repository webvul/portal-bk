package com.kii.extension.sdk.entity.thingif.conditions;

public class LessAndEqThan extends RangeCondition{

	private Object lowerLimit;

	private boolean lowerIncluded;

	@Override
	public ConditionType getType(){
		return ConditionType.eq_lt;
	}

	public Object getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(Object lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public boolean isLowerIncluded() {
		return lowerIncluded;
	}

	public void setLowerIncluded(boolean lowerIncluded) {
		this.lowerIncluded = lowerIncluded;
	}
}
