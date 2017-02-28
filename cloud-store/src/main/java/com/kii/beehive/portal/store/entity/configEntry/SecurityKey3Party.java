package com.kii.beehive.portal.store.entity.configEntry;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SecurityKey3Party extends BeehiveConfig {
	
	public static final String SECURITY_KEY = "securityKey";
	
	private Map<String, String> values = new HashMap<>();
	
	public SecurityKey3Party() {
		
		super.setConfigName(SECURITY_KEY);
	}
	
	
	public Map<String, String> getValues() {
		return values;
	}
	
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
	
	public void addKey(String key, String value) {
		values.put(key, value);
	}
	
	@JsonIgnore
	public String getSecurityKey(String key) {
		return values.get(key);
	}
}
