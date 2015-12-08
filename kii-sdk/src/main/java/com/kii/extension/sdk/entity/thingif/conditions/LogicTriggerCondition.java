package com.kii.extension.sdk.entity.thingif.conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract  class LogicTriggerCondition extends TriggerCondition {

	private List<TriggerCondition> clauses=new ArrayList<>();


	public List<TriggerCondition> getClauses() {
		return clauses;
	}

	public void setClauses(List<TriggerCondition> clauses) {
		this.clauses = clauses;
	}

}
