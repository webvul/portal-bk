package com.kii.extension.ruleengine.store.trigger.condition;


import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.ConditionType;

public class NotLogic extends LogicCol {

	@Override
	public ConditionType getType() {
		return ConditionType.not;
	}

	private Condition clause;

	public Condition getClause() {
		return clause;
	}

	public void setClause(Condition clause) {
		this.clause = clause;
	}

	@Override
	public LogicCol addClause(Condition clause) {
		this.clause=clause;
		return this;
	}
	

}

