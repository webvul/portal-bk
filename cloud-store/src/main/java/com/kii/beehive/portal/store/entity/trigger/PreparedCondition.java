package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.store.entity.trigger.schedule.TriggerValidPeriod;

public class PreparedCondition {

	private TriggerValidPeriod period;

	@JsonUnwrapped
	public TriggerValidPeriod getPeriod() {
		return period;
	}

	public void setPeriod(TriggerValidPeriod period) {
		this.period = period;
	}

}
