package com.kii.beehive.portal.web.exception;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PortalException extends RuntimeException {

	private static final long serialVersionUID = -2799530582371715829L;

	private String errorCode;

	private HttpStatus status;

	public PortalException(){

	}

	public PortalException(String errorCode,HttpStatus status){
		this.errorCode=errorCode;
		this.status=status;
	}

	@JsonIgnore
	@Override
	public StackTraceElement[] getStackTrace(){
		return super.getStackTrace();
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@JsonIgnore
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getErrorCode(){
		return errorCode;
	}


}
