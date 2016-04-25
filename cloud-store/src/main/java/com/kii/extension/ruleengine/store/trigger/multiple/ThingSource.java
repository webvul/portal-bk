package com.kii.extension.ruleengine.store.trigger.multiple;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ThingSource implements SourceElement {

	private long thingID;

	private Set<String> fieldSet=new HashSet<>();

	public Set<String> getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(Set<String> fieldSet) {
		this.fieldSet = fieldSet;
	}

	@JsonIgnore
	public void setStateName(String statusName){
		fieldSet.add(statusName);
	}

	public long getThingID() {
		return thingID;
	}

	public void setThingID(long thingID) {
		this.thingID = thingID;
	}

	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
}
