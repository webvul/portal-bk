package com.kii.beehive.obix.store.beehive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionInputDefine {

	private  String type;

	private Map<String,PointDetail>  properties=new HashMap<>();

	private String title;

	private List<String>  required=new ArrayList<>();

	private String displayNameCN;

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

	public List<String> getRequired() {
		return required;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}

	public String getDisplayNameCN() {
		return displayNameCN;
	}

	public void setDisplayNameCN(String displayNameCN) {
		this.displayNameCN = displayNameCN;
	}
}
