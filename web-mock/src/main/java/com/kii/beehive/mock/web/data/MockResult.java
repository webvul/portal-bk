package com.kii.beehive.mock.web.data;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.extension.sdk.entity.KiiEntity;

public class MockResult extends KiiEntity {

	private JsonNode result;

	private String method;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public JsonNode getResult() {
		return result;
	}

	public void setResult(JsonNode result) {
		this.result = result;
	}
}
