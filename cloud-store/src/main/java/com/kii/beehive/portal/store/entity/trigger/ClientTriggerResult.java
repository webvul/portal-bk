package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.extension.sdk.entity.KiiEntity;

public class ClientTriggerResult extends KiiEntity{

	private String triggerID;

	private String serviceName;

	private JsonNode result;

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public JsonNode getResult() {
		return result;
	}

	public void setResult(JsonNode result) {
		this.result = result;
	}
}
