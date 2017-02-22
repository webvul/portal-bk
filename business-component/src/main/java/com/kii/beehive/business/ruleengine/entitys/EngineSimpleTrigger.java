package com.kii.beehive.business.ruleengine.entitys;

import com.kii.beehive.portal.store.entity.trigger.BeehiveTriggerType;

public class EngineSimpleTrigger extends EngineTrigger {

	public EngineSimpleTrigger(){

	}

	private SingleObject source;

	public SingleObject getSource() {
		return source;
	}

	public void setSource(SingleObject source) {
		this.source = source;
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Simple;
	}


}
