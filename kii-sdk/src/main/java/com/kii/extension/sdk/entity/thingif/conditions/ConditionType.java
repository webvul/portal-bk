package com.kii.extension.sdk.entity.thingif.conditions;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConditionType {


	eq("="),eq_gt(">="),eq_lt("<="),gt(">"),lt("<"),And("and"),Or("or"),Not("not");

	private String value;

	ConditionType(String val){
		this.value=val;
	}


	private static Map<String,ConditionType> valueMap=new HashMap<>();

	static{

		for(ConditionType type:ConditionType.values()){
			valueMap.put(type.toValue(),type);
		}
	}

	@JsonCreator
	public static ConditionType forValue(String value) {
		return valueMap.get(value);
	}

	@JsonValue
	public String toValue() {
		return value;
	}
}
