package com.kii.extension.drools.entity;

import com.google.common.base.Objects;

public class MemberMatchResult {


	private String thingID;

	private String triggerID;

	private boolean result=false;


	public MemberMatchResult(String triggerID,String thingID){
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

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemberMatchResult that = (MemberMatchResult) o;
		return Objects.equal(triggerID,that.triggerID) &&
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
