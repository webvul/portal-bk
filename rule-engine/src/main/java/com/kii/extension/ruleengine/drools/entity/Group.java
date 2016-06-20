package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

import com.kii.extension.ruleengine.store.trigger.Express;

public class Group extends ThingCol implements TriggerData {

	private String triggerID;


	private Express express;


	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {

		this.triggerID = triggerID;
	}

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	@Override
	public String toString() {
		return "Group{" +
				"triggerID='" + triggerID + '\'' +
				", things=" +super.getThings() +
				", name="+super.getName()+
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Group group = (Group) o;
		return Objects.equal(triggerID, group.triggerID) &&
				Objects.equal(name, group.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID, name);
	}
}
