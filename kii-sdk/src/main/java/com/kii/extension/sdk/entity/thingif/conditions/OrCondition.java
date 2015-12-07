package com.kii.extension.sdk.entity.thingif.conditions;

public class OrCondition extends LogicCondition{

	@Override
	public ConditionType getType(){
		return ConditionType.Or;
	}
}
