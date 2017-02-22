package com.kii.beehive.portal.store.entity.trigger.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimplePeriod implements TriggerValidPeriod {
	@Override
	public String getType() {
		return "simple";
	}

	private long startTime;

	private long endTime;

	@JsonProperty("startAt")
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@JsonProperty("endAt")
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
