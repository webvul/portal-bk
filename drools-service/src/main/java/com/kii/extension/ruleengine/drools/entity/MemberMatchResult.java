package com.kii.extension.ruleengine.drools.entity;

import com.google.common.base.Objects;

public class MemberMatchResult {


	private String thingID;

	private int triggerID;

	private boolean result=false;


	public MemberMatchResult(int triggerID,String thingID){
		this.thingID=thingID;
		this.triggerID=triggerID;
		this.result=true;
	}
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemberMatchResult that = (MemberMatchResult) o;
		return triggerID == that.triggerID &&
				Objects.equal(thingID, that.thingID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(thingID, triggerID);
	}

	@Override
	public String toString() {
		return "MemberMatchResult{" +
				"thingID='" + thingID + '\'' +
				", triggerID=" + triggerID +
				", result=" + result +
				'}';
	}
}
