package com.kii.beehive.portal.store.entity;

import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.sdk.entity.serviceextension.ScheduleTriggerConfig;

public class ExtensionCodeEntity extends KiiEntity{

	private String functionName;

	private EventTriggerConfig eventTrigger;

	private ScheduleTriggerConfig  scheduleTrigger;

	private String jsBody;

	private String appID;

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public EventTriggerConfig getEventTrigger() {
		return eventTrigger;
	}

	public void setEventTrigger(EventTriggerConfig eventTrigger) {
		this.eventTrigger = eventTrigger;
	}

	public ScheduleTriggerConfig getScheduleTrigger() {
		return scheduleTrigger;
	}

	public void setScheduleTrigger(ScheduleTriggerConfig scheduleTrigger) {
		this.scheduleTrigger = scheduleTrigger;
	}

	public String getJsBody() {
		return jsBody;
	}

	public void setJsBody(String jsBody) {
		this.jsBody = jsBody;
	}
}
