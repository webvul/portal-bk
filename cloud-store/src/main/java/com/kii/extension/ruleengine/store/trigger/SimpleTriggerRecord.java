package com.kii.extension.ruleengine.store.trigger;

public class SimpleTriggerRecord extends TriggerRecord{

	public SimpleTriggerRecord(){

	}

	private SingleThing source;



	public SingleThing getSource() {
		return source;
	}

	public void setSource(SingleThing source) {
		this.source = source;
	}

	@Override
	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Simple;
	}


}
