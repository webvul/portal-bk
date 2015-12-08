package com.kii.extension.sdk.entity.thingif.conditions;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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


	@JsonIgnore
	public Map<String,Object> getConditionJson() {

		if (this instanceof LogicTriggerCondition) {
			return Collections.singletonMap(this.getType().getValue(), ((LogicTriggerCondition) this).getClauses()
					.stream()
					.map(TriggerCondition::getConditionJson)
					.collect(Collectors.toCollection(ArrayList::new)));
		} else {
			return Collections.singletonMap(this.getType().getValue(), this);
		}
	}


}
