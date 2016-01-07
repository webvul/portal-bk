package com.kii.beehive.portal.exception;

import org.apache.http.HttpStatus;

public class ThingNotExistException extends StoreServiceException{


	public ThingNotExistException(Long thingID){

		super.setErrorCode("ThingNotExistException");

		super.setStatusCode(HttpStatus.SC_NOT_FOUND);

		super.setMessage(" thingID = "+thingID);

	}
}
