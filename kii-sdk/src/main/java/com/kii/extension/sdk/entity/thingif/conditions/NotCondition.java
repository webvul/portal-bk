package com.kii.extension.sdk.entity.thingif.conditions;


public class NotCondition extends Condition {

	private Condition clause;

	@Override
	public ConditionType getType(){
		return ConditionType.Not;
	}

	public Condition getClause(){
		return clause;
	}

	public void setClause(Condition condition){
		clause=condition;
	}
}
