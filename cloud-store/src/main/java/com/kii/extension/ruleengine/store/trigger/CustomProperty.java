package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class CustomProperty {

	private Map<String,Object> property=new HashMap<>();


	@JsonAnyGetter
	public Map<String, Object> getCustom() {
		return property;
	}

	public void setCustom(Map<String, Object> property) {
		this.property = property;
	}

	@JsonAnySetter
	public void addProperty(String key, Object value) {

		property.put(key,value);
	}
}
