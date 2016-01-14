package com.kii.extension.sdk.exception;


import org.apache.http.HttpResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KiiCloudException extends RuntimeException {


	private static final long serialVersionUID = -7579029564962086868L;


	public KiiCloudException(){



	}


	public KiiCloudException(HttpResponse response){
		this.statusCode=response.getStatusLine().getStatusCode();

		this.message=null;

		this.errorCode=null;


	}



	private String errorCode;

	private String message;

	private int statusCode;


	@JsonIgnore
	public int getStatusCode() {
		return statusCode;
	}


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

	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();

	}
}
