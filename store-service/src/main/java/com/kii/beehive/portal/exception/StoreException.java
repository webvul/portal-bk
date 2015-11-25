package com.kii.beehive.portal.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.exception.KiiCloudException;

public class StoreException extends RuntimeException {



	private String errorCode;

	private String message;

	private int statusCode;


	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();

	}
}
