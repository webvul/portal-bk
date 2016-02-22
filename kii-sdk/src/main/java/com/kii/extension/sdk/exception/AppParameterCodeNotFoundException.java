package com.kii.extension.sdk.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AppParameterCodeNotFoundException extends KiiCloudException {

	private String parameterName;

	@JsonIgnore
	public int getStatusCode() {
		return 404;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
}
