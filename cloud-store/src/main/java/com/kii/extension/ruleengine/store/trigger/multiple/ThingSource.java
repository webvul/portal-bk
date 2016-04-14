package com.kii.extension.ruleengine.store.trigger.multiple;

public class ThingSource implements SourceElement {

	private String thingID;

	private String stateName;

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
}
