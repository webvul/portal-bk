package com.kii.beehive.portal.store.entity.trigger;

import com.kii.extension.sdk.entity.thingif.Predicate;

public class FrontCondition {



	private Predicate schedule;


	private AbstractPeriod period;

	public AbstractPeriod getPeriod() {
		return period;
	}

	public void setPeriod(AbstractPeriod period) {
		this.period = period;
	}

	public Predicate getSchedule() {
		return schedule;
	}

	public void setSchedule(Predicate schedule) {
		this.schedule = schedule;
	}

}
