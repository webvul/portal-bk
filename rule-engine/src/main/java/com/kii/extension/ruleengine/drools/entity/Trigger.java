package com.kii.extension.ruleengine.drools.entity;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Objects;

import com.kii.extension.ruleengine.store.trigger.WhenType;

public class Trigger {

	private final String triggerID;

	private TriggerType type;

	private WhenType when;

	private boolean enable=true;

	private boolean isStream=false;


	public Trigger(String triggerID){

		this.triggerID=triggerID;
	}

	public Trigger(Trigger trigger){

		this.triggerID=trigger.getTriggerID();
		BeanUtils.copyProperties(trigger,this);
	}

	public boolean isStream() {
		return isStream;
	}

	public void setStream(boolean stream) {
		isStream = stream;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public WhenType getWhen() {
		return when;
	}

	public void setWhen(WhenType when) {
		this.when = when;
	}

	public TriggerType getType() {
		return type;
	}

	public void setType(TriggerType type) {
		this.type = type;
	}

	public String getTriggerID() {
		return triggerID;
	}



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Trigger trigger = (Trigger) o;
		return Objects.equal(triggerID,trigger.triggerID) ;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}

	@Override
	public String toString() {
		return "Trigger{" +
				"triggerID='" + triggerID +"\'"+
				", type='" + type + '\'' +
				", when='" + when + '\'' +
				", enable=" + enable +
				'}';
	}
}
