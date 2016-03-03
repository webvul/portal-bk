package com.kii.extension.ruleengine.store.trigger.condition;


import com.kii.extension.ruleengine.store.trigger.Condition;

public abstract class LogicCol implements Condition {

	public LogicCol() {

	}
	


	public abstract LogicCol addClause(Condition clause);

}
