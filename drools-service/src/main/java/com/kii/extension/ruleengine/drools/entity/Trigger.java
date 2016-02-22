package com.kii.extension.ruleengine.drools.entity;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;

public class Trigger {

	private int triggerID;

	private int number;

	private Set<String> things=new HashSet<>();

	private String type;

	private String when;


	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things = things;
	}

	public void addThing(String id) {
		this.things.add(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Trigger trigger = (Trigger) o;
		return triggerID == trigger.triggerID;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}

	@Override
	public String toString() {
		return "Trigger{" +
				"triggerID=" + triggerID +
				", number=" + number +
				", things=" + things +
				", type='" + type + '\'' +
				", when='" + when + '\'' +
				'}';
	}
}
