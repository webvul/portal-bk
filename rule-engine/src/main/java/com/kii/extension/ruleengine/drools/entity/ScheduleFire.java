package com.kii.extension.ruleengine.drools.entity;

import java.util.Objects;

public class ScheduleFire implements WithTrigger{

	private String triggerID;

	private boolean enable;

	public void reset(){
		enable=false;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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
		ScheduleFire that = (ScheduleFire) o;
		return Objects.equals(triggerID, that.triggerID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(triggerID);
	}
}
