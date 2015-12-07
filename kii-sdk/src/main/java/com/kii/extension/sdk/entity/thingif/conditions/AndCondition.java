package com.kii.extension.sdk.entity.thingif.conditions;

public class AndCondition extends LogicCondition {

	@Override
	public ConditionType getType(){
		return ConditionType.And;
	}

}
