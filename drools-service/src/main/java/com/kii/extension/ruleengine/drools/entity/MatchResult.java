package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class MatchResult {

	private int triggerID;

	private boolean result;

	public MatchResult(int triggerID){
		this.triggerID=triggerID;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MatchResult that = (MatchResult) o;
		return triggerID == that.triggerID;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}

	@Override
	public String toString() {
		return "MatchResult{" +
				"triggerID=" + triggerID +
				'}';
	}
}
