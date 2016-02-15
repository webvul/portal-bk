package com.kii.extension.ruleengine.thingtrigger;

public class MemberMatchResult {


	private String thingID;

	private int triggerID;

//	private boolean result;

	public MemberMatchResult(int triggerID,String thingID){
		this.thingID=thingID;
		this.triggerID=triggerID;
	}


	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

}
