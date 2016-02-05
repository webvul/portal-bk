package com.kii.extension.ruleengine.thingtrigger;

import java.util.HashSet;
import java.util.Set;

public class Trigger {

	private int triggerID;

	private int number;

	private Set<String> things=new HashSet<>();

	private String express;

	private String type;

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

	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things = things;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}
	
	public void addThing(String id) {
		this.things.add(id);
	}
}
