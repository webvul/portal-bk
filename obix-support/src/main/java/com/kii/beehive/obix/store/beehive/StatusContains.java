package com.kii.beehive.obix.store.beehive;

import java.util.HashMap;
import java.util.Map;

public class StatusContains {

	private String type;

	private String title;

	private Map<String,PointDetail> properties=new HashMap<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, PointDetail> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PointDetail> properties) {
		this.properties = properties;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
