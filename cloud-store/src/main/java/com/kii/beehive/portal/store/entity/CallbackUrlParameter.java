package com.kii.beehive.portal.store.entity;

import com.kii.extension.sdk.entity.KiiEntity;

public class CallbackUrlParameter extends KiiEntity {



	private String baseUrl;

	private String stateChange;

	private String thingCreated;

	public String getThingCreated() {
		return thingCreated;
	}

	public void setThingCreated(String thingCreated) {
		this.thingCreated = thingCreated;
	}

	public String getStateChange() {
		return stateChange;
	}

	public void setStateChange(String stateChange) {
		this.stateChange = stateChange;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
