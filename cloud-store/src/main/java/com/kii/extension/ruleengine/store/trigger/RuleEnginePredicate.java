package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RuleEnginePredicate {

	private SchedulePrefix  schedule;

	private String  express;



	private WhenType triggersWhen;

	private Condition condition;

	public WhenType getTriggersWhen() {
		return triggersWhen;
	}

	public void setTriggersWhen(WhenType triggersWhen) {
		this.triggersWhen = triggersWhen;
	}

	public void setCondition(Condition condition) {
		this.condition=condition;
	}

	@JsonProperty("condition")
	public Condition getCondition(){
		return condition;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public SchedulePrefix getSchedule() {
		return schedule;
	}

	public void setSchedule(SchedulePrefix schedule) {
		this.schedule = schedule;
	}
}
