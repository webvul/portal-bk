package com.kii.beehive.portal.store.entity.trigger;

public class IntervalPrefix implements SchedulePrefix {

	private TimerUnitType timeUnit;

	private int interval;

	@Override
	public String getType() {
		return "Interval";
	}

	public TimerUnitType getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimerUnitType timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
