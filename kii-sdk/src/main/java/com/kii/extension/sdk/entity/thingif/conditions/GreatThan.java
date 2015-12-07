package com.kii.extension.sdk.entity.thingif.conditions;

public class GreatThan extends GreatAndEqThan{

	private Object upperLimit;

	private boolean upperIncluded;


	@Override
	public ConditionType getType(){
		return ConditionType.gt;
	}

	@Override
	public Object getUpperLimit() {

		return false;
	}

}
