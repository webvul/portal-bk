package com.kii.extension.ruleengine.drools.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Objects;

public class Trigger {

	private String triggerID;

	private int number;

	private Set<String> things=new HashSet<>();

	private String type;

	private String when;

	private boolean enable=true;

	private boolean isStream=false;

	public Trigger(){

	}

	public Trigger(Trigger trigger){

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

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things = things;
	}

	public void addThing(String id) {
		this.things.add(id);
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
				", number=" + number +
				", things=" + things +
				", type='" + type + '\'' +
				", when='" + when + '\'' +
				", enable=" + enable +
				'}';
	}
}
