package com.kii.beehive.portal.web.exception;

import org.springframework.http.HttpStatus;

import com.kii.beehive.portal.exception.BusinessException;

public class PortalException extends BusinessException {

	private static final long serialVersionUID = -2799530582371715829L;

	public PortalException(String errorCode,HttpStatus status){
		this.setErrorCode(errorCode);
		super.setStatusCode(status.value());
	}

	public HttpStatus getStatus(){

		return HttpStatus.valueOf(super.getStatusCode());
	}

}
