package com.kii.extension.ruleengine.drools.entity;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Objects;

public class UnitSource {

	private String  triggerID;

	private String unitName;


	private String type;


	private Collection<String> things=new HashSet<>();


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public Collection<String> getThings() {
		return things;
	}

	public void setThings(Collection<String> things) {
		this.things = things;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UnitSource that = (UnitSource) o;
		return Objects.equal(triggerID, that.triggerID) &&
				Objects.equal(unitName, that.unitName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, unitName);
	}

	@Override
	public String toString() {
		return "UnitSource{" +
				"triggerID='" + triggerID + '\'' +
				", unitName='" + unitName + '\'' +
				", things='" + things + '\'' +
				'}';
	}
}
