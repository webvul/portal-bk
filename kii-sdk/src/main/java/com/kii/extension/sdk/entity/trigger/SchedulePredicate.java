package com.kii.extension.sdk.entity.trigger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchedulePredicate extends Predicate {

	@Override
	public EventSourceType getEventSource() {
		return EventSourceType.schedule;
	}

	private String cron;

	@JsonProperty("schedule")
	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
