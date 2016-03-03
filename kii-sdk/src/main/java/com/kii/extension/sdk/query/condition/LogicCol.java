package com.kii.extension.sdk.query.condition;

import com.kii.extension.sdk.query.Condition;

public abstract class LogicCol implements Condition {

	public LogicCol() {

	}
	


	public abstract LogicCol addClause(Condition clause);

}
