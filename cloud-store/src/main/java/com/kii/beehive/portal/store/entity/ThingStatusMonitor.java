package com.kii.beehive.portal.store.entity;

import java.util.Set;

import com.kii.extension.ruleengine.store.trigger.Condition;


public class ThingStatusMonitor {

	private Set<Long> things;

	private Condition condition;

	private String express;

	private String relationTriggerID;

	private String creator;

	private boolean enable;

	public Set<Long> getThings() {
		return things;
	}

	public void setThings(Set<Long> things) {
		this.things = things;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getRelationTriggerID() {
		return relationTriggerID;
	}

	public void setRelationTriggerID(String relationTriggerID) {
		this.relationTriggerID = relationTriggerID;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
