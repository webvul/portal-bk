package com.kii.beehive.portal.store.entity.trigger;

public class SimpleTriggerRuntimeState extends TriggerRuntimeState {

	private String triggerID;

//	private String thingID;

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public BeehiveTriggerType getType() {
		return BeehiveTriggerType.Simple;
	}

//	public String getThingID() {
//		return thingID;
//	}
//
//	public void setThingID(String thingID) {
//		this.thingID = thingID;
//	}
}
