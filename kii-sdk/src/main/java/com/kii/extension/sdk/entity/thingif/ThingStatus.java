package com.kii.extension.sdk.entity.thingif;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ThingStatus {

	private Map<String,Object> fields=new HashMap<>();


	@JsonAnyGetter
	public Map<String, Object> getFields() {
		return fields;
	}

	@JsonIgnore
	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	@JsonAnySetter
	public void setField(String key,Object value){
		this.fields.put(key,value);
	}}
