package com.kii.extension.ruleengine.store.trigger.groups;

public class TriggerGroupPolicy {

	private TriggerGroupPolicyType groupPolicy=TriggerGroupPolicyType.All;

	private int criticalNumber=0;

	public int getCriticalNumber() {
		return criticalNumber;
	}

	public void setCriticalNumber(int criticalNumber) {
		this.criticalNumber = criticalNumber;
	}

	public TriggerGroupPolicyType getGroupPolicy() {
		return groupPolicy;
	}

	public void setGroupPolicy(TriggerGroupPolicyType groupPolicy) {
		this.groupPolicy = groupPolicy;
	}


}
