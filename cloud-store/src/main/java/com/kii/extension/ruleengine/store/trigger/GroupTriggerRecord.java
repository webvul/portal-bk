package com.kii.extension.ruleengine.store.trigger;

public class GroupTriggerRecord extends TriggerRecord{

	private TriggerSource  source;

	private TriggerGroupPolicy  policy;


	public TriggerGroupPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicy policy) {
		this.policy = policy;
	}

	public TriggerSource getSource() {
		return source;
	}

	public void setSource(TriggerSource source) {
		this.source = source;
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Group;
	}
}
