package com.kii.extension.sdk.entity.thingif.conditions;


public class NotTriggerCondition extends TriggerCondition {

	private TriggerCondition clause;

	@Override
	public ConditionType getType(){
		return ConditionType.Not;
	}

	public TriggerCondition getClause(){
		return clause;
	}

	public void setClause(TriggerCondition condition){
		clause=condition;
	}
}
