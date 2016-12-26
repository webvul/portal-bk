package com.kii.extension.ruleengine.store.trigger.groups;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

public class GroupTriggerRecord extends TriggerRecord {

	private ThingCollectSource source;

	private TriggerGroupPolicy  policy;


	public TriggerGroupPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(TriggerGroupPolicy policy) {
		this.policy = policy;
	}

	@JsonIgnore
	public void setPolicyType(TriggerGroupPolicyType type){
		this.policy=new TriggerGroupPolicy();
		this.policy.setGroupPolicy(type);
	}

	@JsonIgnore
	public void setPolicyType(TriggerGroupPolicyType type,int number){
		this.policy=new TriggerGroupPolicy();
		this.policy.setGroupPolicy(type);
		this.policy.setCriticalNumber(number);
	}

	public ThingCollectSource getSource() {
		return source;
	}

//	@JsonIgnore
//	public void setTagSelector(TagSelector selector){
//
//		this.source=new TriggerSource();
//		source.setSelector(selector);
//	}


	public void setSource(ThingCollectSource source) {
		this.source = source;
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Group;
	}
}
