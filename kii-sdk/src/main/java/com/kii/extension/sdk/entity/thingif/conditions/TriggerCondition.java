package com.kii.extension.sdk.entity.thingif.conditions;


import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;


public abstract  class TriggerCondition {

	private ConditionType type;


	@JsonIgnore
	public abstract ConditionType getType();

	public void setType(ConditionType type) {
		this.type = type;
	}





}
