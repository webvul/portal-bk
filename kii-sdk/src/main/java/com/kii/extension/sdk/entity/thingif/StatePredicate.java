package com.kii.extension.sdk.entity.thingif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.extension.sdk.commons.ConditionConvert;
import com.kii.extension.sdk.query.Condition;

public class StatePredicate extends Predicate {

	private TriggerWhen triggersWhen;

	private Condition condition;

	@Override
	public EventSourceType getEventSource() {
		return EventSourceType.states;
	}

	public TriggerWhen getTriggersWhen() {
		return triggersWhen;
	}

	public void setTriggersWhen(TriggerWhen triggersWhen) {
		this.triggersWhen = triggersWhen;
	}

	public void setCondition(Condition condition) {
		this.condition=condition;
	}

	@JsonProperty("condition")
	public Condition getCondition(){
		return condition;
	}
}
