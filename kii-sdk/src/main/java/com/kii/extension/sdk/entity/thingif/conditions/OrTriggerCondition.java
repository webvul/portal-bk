package com.kii.extension.sdk.entity.thingif.conditions;

public class OrTriggerCondition extends LogicTriggerCondition {

	@Override
	public ConditionType getType(){
		return ConditionType.Or;
	}
}
