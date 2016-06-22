package com.kii.extension.ruleengine.drools.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Summary  extends  TriggerData{


	private String funName;

	private String fieldName;

	protected Set<String> things=new HashSet<>();


	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things=things;
	}


	public void addThing(String thing){
		things.add(thing);
	}

	public void setThingCol(Collection<String> things) {
		this.things.addAll(things);
	}


	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	@Override
	public String toString() {
		return "Summary{" +
				"triggerID='" + super.getTriggerID() + '\'' +
				", funName='" + funName + '\'' +
				", fieldName='" + fieldName + '\'' +
				", name='" + super.getName() + '\'' +
				", things=" + things +
				'}';
	}
}
