package com.kii.extension.ruleengine.store.trigger.multiple;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.Express;

public class ThingSource implements SourceElement {

	private String thingID;

	private boolean allStatus=true;

	@JsonUnwrapped
	private Express express=new Express();


	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public boolean isAllStatus() {
		return allStatus;
	}

	public void setAllStatus(boolean allStatus) {
		this.allStatus = allStatus;
	}

	private Set<String> fieldSet=new HashSet<>();

	public Set<String> getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(Set<String> fieldSet) {
		this.fieldSet = fieldSet;
	}

	@JsonIgnore
	public void addStateName(String statusName){
		fieldSet.add(statusName);
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
}
