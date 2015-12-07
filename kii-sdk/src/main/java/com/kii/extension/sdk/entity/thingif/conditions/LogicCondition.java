package com.kii.extension.sdk.entity.thingif.conditions;

import java.util.ArrayList;
import java.util.List;

public class LogicCondition extends Condition {

	private List<Condition> clauses=new ArrayList<>();


	public List<Condition> getClauses() {
		return clauses;
	}

	public void setClauses(List<Condition> clauses) {
		this.clauses = clauses;
	}
}
