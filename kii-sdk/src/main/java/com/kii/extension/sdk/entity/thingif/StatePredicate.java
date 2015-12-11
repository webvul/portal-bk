package com.kii.extension.sdk.entity.thingif;

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

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
}
