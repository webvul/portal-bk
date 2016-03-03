package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

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
