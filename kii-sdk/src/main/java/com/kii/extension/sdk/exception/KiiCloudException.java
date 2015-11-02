package com.kii.extension.sdk.exception;


import com.fasterxml.jackson.annotation.JsonProperty;

public class KiiCloudException extends RuntimeException {




	public KiiCloudException(){



	}

	private String errorCode;

	private String message;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@JsonProperty("message")
	public String getErrorMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
