package com.kii.extension.ruleengine.sdk.query.condition;

import com.kii.extension.ruleengine.sdk.query.Condition;

public abstract class LogicCol implements Condition {

	public LogicCol() {

	}
	


	public abstract LogicCol addClause(Condition clause);

}
