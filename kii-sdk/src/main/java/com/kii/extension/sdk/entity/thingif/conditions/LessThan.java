package com.kii.extension.sdk.entity.thingif.conditions;

public class LessThan extends LessAndEqThan{

	private Object lowerLimit;

	@Override
	public ConditionType getType(){
		return ConditionType.lt;
	}

	@Override
	public boolean isLowerIncluded() {
		return false;
	}

}
