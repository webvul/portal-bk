package com.kii.beehive.portal.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StoreException extends RuntimeException {

	private static final long serialVersionUID = -5828561870661219029L;

	private String errorCode;

	private String message;

	private int statusCode;

	public StoreException(){

	}

	public StoreException(String message) {
		this.message = message;
	}

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
