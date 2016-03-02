package com.kii.extension.store.trigger.condition;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.store.trigger.Condition;
import com.kii.extension.store.trigger.ConditionType;


public class AndLogic extends LogicCol {

	@Override
	public ConditionType getType() {
		return ConditionType.and;
	}

	private List<Condition> clauses = new ArrayList<Condition>();

	public List<Condition> getClauses() {
		return clauses;
	}

	public void setClauses(List<Condition> clauses) {
		this.clauses = clauses;
	}

	@Override
	public LogicCol addClause(Condition clause) {
		this.clauses.add(clause);
		return this;
	}
	
	
	
}
