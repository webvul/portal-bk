package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicyType;

public class Group extends ThingCol implements TriggerData {

	private String triggerID;


	private TriggerGroupPolicyType policy=TriggerGroupPolicyType.None;

	private int number;

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {

		this.triggerID = triggerID;
	}


	public TriggerGroupPolicyType getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicyType policy) {
		this.policy = policy;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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
