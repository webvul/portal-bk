package com.kii.beehive.portal.web.exception;

import org.springframework.http.HttpStatus;

import com.kii.beehive.portal.exception.BusinessException;

public class PortalException extends BusinessException {

	private static final long serialVersionUID = -2799530582371715829L;

	public PortalException(ErrorCode errorCode){
		this.setErrorCode(errorCode.getName());
		super.setStatusCode(errorCode.getStatus());
	}

	public PortalException(ErrorCode errorCode,String... params){
		super(params);
		this.setErrorCode(errorCode.getName());
		super.setStatusCode(errorCode.getStatus());
	}


	public HttpStatus getStatus(){

		return HttpStatus.valueOf(super.getStatusCode());
	}

}
