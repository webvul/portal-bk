package com.kii.extension.ruleengine.store.trigger.schedule;

import com.kii.extension.ruleengine.store.trigger.schedule.SchedulePrefix;
import com.kii.extension.ruleengine.store.trigger.schedule.TimerUnitType;

public class IntervalPrefix implements SchedulePrefix {

	private TimerUnitType timeUnit;

	private int interval;

//	private int delay;

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

//	public int getDelay() {
//		return delay;
//	}
//
//	public void setDelay(int delay) {
//		this.delay = delay;
//	}
}
