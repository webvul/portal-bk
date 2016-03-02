package com.kii.extension.drools.entity;

import com.google.common.base.Objects;

public class MatchResult {

	private String triggerID;


	public MatchResult(String triggerID){
		this.triggerID=triggerID;
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
		MatchResult that = (MatchResult) o;
		return  Objects.equal(triggerID,that.triggerID) ;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}

	@Override
	public String toString() {
		return "MatchResult{" +
				"triggerID='" + triggerID +"\'"+
				'}';
	}
}
