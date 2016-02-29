package com.kii.beehive.portal.store.entity.trigger;

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
