package com.kii.beehive.portal.store.entity.trigger;

import java.util.HashMap;
import java.util.Map;

public class ExternalEntry {
	
	
	
	private Map<String,Object> values=new HashMap<>();
	
	public Map<String, Object> getValues() {
		return values;
	}
	
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	
}
