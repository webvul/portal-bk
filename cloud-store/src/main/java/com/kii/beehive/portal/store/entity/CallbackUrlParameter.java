package com.kii.beehive.portal.store.entity;

import com.kii.extension.sdk.entity.KiiEntity;

public class CallbackUrlParameter extends KiiEntity {


	private String simple;

	private String positive;

	private String summary;

	private String negitive;

	private String baseUrl;

	private String stateChange;

	public String getStateChange() {
		return stateChange;
	}

	public void setStateChange(String stateChange) {
		this.stateChange = stateChange;
	}

	public String getPositive() {
		return positive;
	}

	public void setPositive(String positive) {
		this.positive = positive;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getNegitive() {
		return negitive;
	}

	public void setNegitive(String negitive) {
		this.negitive = negitive;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getSimple() {
		return simple;
	}

	public void setSimple(String simple) {
		this.simple = simple;
	}
}
