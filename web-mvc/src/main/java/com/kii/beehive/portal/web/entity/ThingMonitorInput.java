package com.kii.beehive.portal.web.entity;

import java.util.Set;

import com.kii.extension.sdk.entity.trigger.TriggerConditionEntry;


public class ThingMonitorInput {

	private String name;

	private Set<String> things;

	private String express;

	private TriggerConditionEntry condition;

	private boolean enable;
	
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

	public TriggerConditionEntry getCondition() {
		return condition;
	}

	public void setCondition(TriggerConditionEntry condition) {
		this.condition = condition;
	}
}
