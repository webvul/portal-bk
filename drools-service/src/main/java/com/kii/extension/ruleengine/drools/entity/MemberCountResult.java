package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class MemberCountResult {


	private  int triggerID;

	private int count;

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemberCountResult that = (MemberCountResult) o;
		return triggerID == that.triggerID;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}
}
