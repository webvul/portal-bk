package com.kii.extension.sdk.entity.thingif.conditions;

public class AndTriggerCondition extends LogicTriggerCondition {

	@Override
	public ConditionType getType(){
		return ConditionType.And;
	}

}
