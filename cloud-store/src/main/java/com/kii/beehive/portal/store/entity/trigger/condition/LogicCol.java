package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.Condition;

public abstract class LogicCol implements Condition {

	public LogicCol() {

	}
	


	public abstract LogicCol addClause(Condition clause);

}
