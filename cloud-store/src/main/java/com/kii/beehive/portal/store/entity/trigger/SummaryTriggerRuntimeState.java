package com.kii.beehive.portal.store.entity.trigger;

import java.util.HashMap;
import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class SummaryTriggerRuntimeState extends KiiEntity {

	private Map<String,String> currThingTriggerMap=new HashMap<>();

	private String summaryThingID;

	public Map<String, String> getCurrThingTriggerMap() {
		return currThingTriggerMap;
	}

	public void setCurrThingTriggerMap(Map<String,String> currThingTriggerMap) {
		this.currThingTriggerMap = currThingTriggerMap;
	}

	public String getSummaryThingID() {
		return summaryThingID;
	}

	public void setSummaryThingID(String summaryThingID) {
		this.summaryThingID = summaryThingID;
	}
}
