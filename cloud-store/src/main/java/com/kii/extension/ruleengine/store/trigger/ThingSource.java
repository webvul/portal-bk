package com.kii.extension.ruleengine.store.trigger;

public class ThingSource implements SourceElement {

	private String thingID;

	private String  express;

	private Condition condition;


	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

//	public WhenType getTriggersWhen() {
//		return triggersWhen;
//	}
//
//	public void setTriggersWhen(WhenType triggersWhen) {
//		this.triggersWhen = triggersWhen;
//	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
}
