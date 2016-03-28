package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class UnitResult {

	private String triggerID;

	private String unitName;

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Override
	public String toString() {
		return "UnitResult{" +
				"triggerID='" + triggerID + '\'' +
				", unitName='" + unitName + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UnitResult that = (UnitResult) o;
		return Objects.equal(triggerID, that.triggerID) &&
				Objects.equal(unitName, that.unitName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, unitName);
	}
}
