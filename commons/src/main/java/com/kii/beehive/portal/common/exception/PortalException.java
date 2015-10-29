package com.kii.beehive.portal.common.exception;

import org.springframework.http.HttpStatus;

public class PortalException extends RuntimeException {

	public String getErrorCode(){
		return null;
	}

	public HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

}
