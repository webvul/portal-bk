package com.kii.beehive.portal.exception;

import org.apache.http.HttpStatus;

public class EntryNotFoundException extends StoreServiceException{


	public EntryNotFoundException(String objectID){

		super.setErrorCode("BeehiveObjectNotExist");

		super.setStatusCode(HttpStatus.SC_NOT_FOUND);

		super.setMessage(" entity with objectID is "+objectID+" not exist in beehive");

	}

	public EntryNotFoundException(String errorCode, String message) {

		super.setErrorCode("BeehiveObjectNotExist");

		super.setStatusCode(HttpStatus.SC_NOT_FOUND);

		super.setMessage(message);


	}
}
