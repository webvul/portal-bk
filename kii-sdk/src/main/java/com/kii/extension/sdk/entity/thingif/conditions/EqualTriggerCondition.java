package com.kii.extension.sdk.entity.thingif.conditions;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class EqualTriggerCondition extends TriggerCondition {

	private String field;

	private Object value;

	@JsonIgnore
	@Override
	public ConditionType getType(){
		return ConditionType.eq;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}


}
