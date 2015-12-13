package com.kii.beehive.portal.exception;

public class EntryNotFoundException extends StoreServiceException{


	public EntryNotFoundException(String objectID){

		super.setErrorCode("BeehiveObjectNotExist");

		super.setStatusCode(404);

		super.setMessage(" entity with objectID is "+objectID+" not exist in beehive");

	}
}
