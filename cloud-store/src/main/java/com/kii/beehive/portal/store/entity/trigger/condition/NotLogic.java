package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.Condition;
import com.kii.beehive.portal.store.entity.trigger.ConditionType;

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

