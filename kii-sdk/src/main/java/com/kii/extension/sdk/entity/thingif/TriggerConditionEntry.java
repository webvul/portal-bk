package com.kii.extension.sdk.entity.thingif;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.Trigger;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TriggerConditionEntry {

	private String express;

	private String field;

	private Object value;

	public TriggerConditionEntry(){

	}


	@JsonCreator
	public TriggerConditionEntry(JsonNode node){

		express=node.fieldNames().next();

		if(express.equals("and")||express.equals("or")){
			return;
		}

		field=node.get("field").asText();
		JsonNode jsonVal=node.get("value");
		if(jsonVal.isNumber()){
			value=jsonVal.numberValue();
		}else if(jsonVal.isBoolean()){
			value=jsonVal.asBoolean();
		}else if(jsonVal.isTextual()){
			value=jsonVal.asText();
		}
	}

	@JsonAnyGetter
	public Map<String,Object> getJson(){

		Map<String,Object> params=new HashMap<>();
		params.put("field",field);
		params.put("value",value);

		return Collections.singletonMap(express,params);
	}

	@JsonIgnore
	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}


	@JsonIgnore
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@JsonIgnore
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
