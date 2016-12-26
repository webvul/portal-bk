package com.kii.extension.ruleengine.store.trigger;

public class SimpleTriggerRecord extends TriggerRecord{

	public SimpleTriggerRecord(){

	}

	private ThingSource source;



	public ThingSource getSource() {
		return source;
	}

	public void setSource(ThingSource source) {
		this.source = source;
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Simple;
	}


}
