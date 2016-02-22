package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class TriggerResult {

	private int triggerID;


	public TriggerResult(int triggerID){
		this.triggerID=triggerID;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TriggerResult that = (TriggerResult) o;
		return triggerID == that.triggerID;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}
}
