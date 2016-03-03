package com.kii.beehive.portal.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.ruleengine.sdk.entity.thingif.ThingStatus;

public class StateUpload {

	private ThingStatus state;

	private String target;


	public ThingStatus getState() {
		return state;
	}

	public void setState(ThingStatus state) {
		this.state = state;
	}


	@JsonIgnore
	public String getThingID(){

		int idx=target.indexOf(":");

		return target.substring(idx+1);

	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
