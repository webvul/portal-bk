package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.thingif.StatePredicate;

public class CombinePredicate {

	private StatePredicate  predicate;

	private SchedulePrefix  schedule;

	private String  express;

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	@JsonUnwrapped
	public StatePredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(StatePredicate predicate) {
		this.predicate = predicate;
	}

	public SchedulePrefix getSchedule() {
		return schedule;
	}

	public void setSchedule(SchedulePrefix schedule) {
		this.schedule = schedule;
	}
}
