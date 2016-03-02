package com.kii.extension.store.trigger.condition;


import com.kii.extension.store.trigger.Condition;

public abstract class LogicCol implements Condition {

	public LogicCol() {

	}
	


	public abstract LogicCol addClause(Condition clause);

}
