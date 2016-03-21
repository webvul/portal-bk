package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class UnitSource {

	private String  triggerID;

	private String unitName;

	private String thingID;

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

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UnitSource that = (UnitSource) o;
		return Objects.equal(triggerID, that.triggerID) &&
				Objects.equal(unitName, that.unitName) &&
				Objects.equal(thingID, that.thingID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, unitName, thingID);
	}

	@Override
	public String toString() {
		return "UnitSource{" +
				"triggerID='" + triggerID + '\'' +
				", unitName='" + unitName + '\'' +
				", thingID='" + thingID + '\'' +
				'}';
	}
}
