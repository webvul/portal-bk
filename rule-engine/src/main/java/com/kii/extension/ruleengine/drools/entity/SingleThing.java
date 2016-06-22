package com.kii.extension.ruleengine.drools.entity;

public class SingleThing extends  TriggerData{


	private String thingID;

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public String toString() {
		return "Thing{" +
				"triggerID='" + super.getTriggerID() + '\'' +
				", name='" + super.getName() + '\'' +
				", thingID='" + thingID + '\'' +
				'}';
	}
}
