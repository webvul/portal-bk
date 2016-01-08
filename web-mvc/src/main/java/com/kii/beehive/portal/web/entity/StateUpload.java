package com.kii.beehive.portal.web.entity;

import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class StateUpload {

	private ThingStatus state;

	private String target;


	public ThingStatus getState() {
		return state;
	}

	public void setState(ThingStatus state) {
		this.state = state;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
