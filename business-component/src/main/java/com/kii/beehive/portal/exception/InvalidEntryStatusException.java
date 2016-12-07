package com.kii.beehive.portal.exception;

import org.apache.http.HttpStatus;

public class InvalidEntryStatusException extends BusinessException {
	
	public InvalidEntryStatusException(String beanName,String fieldName,String status){
		
		super.setErrorCode("INVALID_STATUS");
		
		super.setStatusCode(HttpStatus.SC_CONFLICT);
		
		super.addParam("beanName",beanName);
		super.addParam("fieldName",fieldName);
		super.addParam("status",status);
		
		
	}
	
}
