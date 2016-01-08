package com.kii.extension.sdk.exception;

public class AppParameterCodeNotFoundException extends KiiCloudException {

	private String parameterName;

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
}
