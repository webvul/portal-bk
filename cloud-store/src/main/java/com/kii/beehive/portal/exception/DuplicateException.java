package com.kii.beehive.portal.exception;


import org.apache.http.HttpStatus;

public class DuplicateException extends StoreServiceException{


	public DuplicateException(String name){

		super.setErrorCode("DuplicateObject");

		super.setStatusCode(HttpStatus.SC_BAD_REQUEST);

		super.setMessage(" Duplicate Object : "+name);

	}
}
