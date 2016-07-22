package com.kii.beehive.portal.exception;


import org.apache.http.HttpStatus;

public class DuplicateException extends BusinessException{


	public DuplicateException(String name,String type){

		super.setErrorCode("DUPLICATE_OBJECT");

		super.setStatusCode(HttpStatus.SC_CONFLICT);

		super.addParam("objectName",name);
		super.addParam("type",type);
	}
}
