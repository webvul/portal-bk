package com.kii.extension.ruleengine.drools.entity;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;

public class Thing implements TriggerData{

	private String triggerID;

	private String name;

	private String thingID;

	private Set<String> fieldSet=new HashSet<>();

	public Set<String> getFielSet() {
		return fieldSet;
	}

	public void setFieldSet(Set<String> fieldSet) {
		this.fieldSet = fieldSet;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		Thing thing = (Thing) o;
		return Objects.equal(triggerID, thing.triggerID) &&
				Objects.equal(name, thing.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, name);
	}
}
