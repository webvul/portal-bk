package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SummaryStateEntry {

	private String stateListenerID;

	private String tagListenerID;

	private List<String> thingIDs=new ArrayList<>();

	private Map<String,Object> summary=new HashMap<>();


	public Map<String, Object> getSummary() {
		return summary;
	}

	public void setSummary(Map<String, Object> summary) {
		this.summary = summary;
	}

	@JsonIgnore
	public void setSummaryField(String aliasName,Object value){
		summary.put(aliasName,value);
	}

	public Object getSummaryByField(String name,String aliasName){

		return summary.get(name+"."+aliasName);

	}
	public List<String> getThingIDs() {
		return thingIDs;
	}

	public void setThingIDs(List<String> thingIDs) {
		this.thingIDs = thingIDs;
	}

	public String getStateListenerID() {
		return stateListenerID;
	}

	public void setStateListenerID(String stateListenerID) {
		this.stateListenerID = stateListenerID;
	}

	public String getTagListenerID() {
		return tagListenerID;
	}

	public void setTagListenerID(String tagListenerID) {
		this.tagListenerID = tagListenerID;
	}
}
