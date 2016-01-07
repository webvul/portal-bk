package com.kii.beehive.portal.exception;

import org.apache.http.HttpStatus;

public class UserNotExistException extends StoreServiceException{


	public UserNotExistException(String userID){

		super.setErrorCode("BeehiveUserNotExist");

		super.setStatusCode(HttpStatus.SC_NOT_FOUND);

		super.setMessage(" user with userID "+userID+" not existing in beehive");

	}
}
