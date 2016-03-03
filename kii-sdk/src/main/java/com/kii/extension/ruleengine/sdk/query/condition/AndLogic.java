package com.kii.extension.ruleengine.sdk.query.condition;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.ruleengine.sdk.query.Condition;
import com.kii.extension.ruleengine.sdk.query.ConditionType;

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
