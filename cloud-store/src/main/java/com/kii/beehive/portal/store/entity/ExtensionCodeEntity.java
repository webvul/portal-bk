package com.kii.beehive.portal.store.entity;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.ruleengine.sdk.entity.KiiEntity;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.EventTriggerConfig;
import com.kii.extension.ruleengine.sdk.entity.serviceextension.ScheduleTriggerConfig;

public class ExtensionCodeEntity extends KiiEntity{

	private String functionName;

	private List<EventTriggerConfig> eventTrigger=new ArrayList<>();

	private List<ScheduleTriggerConfig> scheduleTrigger=new ArrayList<>();

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

	public List<EventTriggerConfig> getEventTrigger() {
		return eventTrigger;
	}

	public void setEventTrigger(List<EventTriggerConfig> eventTrigger) {
		this.eventTrigger = eventTrigger;
	}

	public void addEventTrigger(EventTriggerConfig  eventTriggerConfig){
		this.eventTrigger.add(eventTriggerConfig);
	}

	public List<ScheduleTriggerConfig> getScheduleTrigger() {
		return scheduleTrigger;
	}

	public void setScheduleTrigger(List<ScheduleTriggerConfig> scheduleTrigger) {
		this.scheduleTrigger = scheduleTrigger;
	}

	public void addScheduleTrgger(ScheduleTriggerConfig  config){
		this.scheduleTrigger.add(config);
	}

	public String getJsBody() {
		return jsBody;
	}

	public void setJsBody(String jsBody) {
		this.jsBody = jsBody;
	}
}
