package com.kii.beehive.portal.web.help;

import org.springframework.http.HttpStatus;

public class PortalException extends RuntimeException {

	private static final long serialVersionUID = -2799530582371715829L;

	private String errorCode;

	private String errorMessage;

	private HttpStatus status;

	public PortalException(){

	}

	public PortalException(String errorCode,String errorMsg,HttpStatus status){
		this.errorCode=errorCode;
		this.errorMessage=errorMsg;
		this.status=status;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getErrorCode(){
		return errorCode;
	}

	public HttpStatus getHttpStatus() {
		return status;
	}

}
