package com.kii.beehive.portal.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloEntry {

	@JsonProperty("name")
	private String name;

	@JsonProperty("val")
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
