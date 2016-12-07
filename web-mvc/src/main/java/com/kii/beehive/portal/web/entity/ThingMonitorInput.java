package com.kii.beehive.portal.web.entity;

import java.util.Set;

import com.kii.extension.ruleengine.store.trigger.Condition;


public class ThingMonitorInput {
	
	private String monitorID;

	private String name;

	private Set<String> things;

	private String express;

	private Condition condition;
	
	private String description;

	private boolean enable;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMonitorID() {
		return monitorID;
	}
	
	public void setMonitorID(String monitorID) {
		this.monitorID = monitorID;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
}
