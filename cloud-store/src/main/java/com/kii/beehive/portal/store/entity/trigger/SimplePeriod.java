package com.kii.beehive.portal.store.entity.trigger;

public class SimplePeriod implements TriggerValidPeriod {
	@Override
	public String getType() {
		return "Simple";
	}

	private long startTime;

	private long endTime;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}