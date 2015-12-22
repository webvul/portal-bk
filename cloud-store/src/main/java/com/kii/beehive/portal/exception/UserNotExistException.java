package com.kii.beehive.portal.exception;

public class UserNotExistException extends StoreException{


	public UserNotExistException(String userID){

		super.setErrorCode("BeehiveUserNotExist");

		super.setStatusCode(404);

		super.setMessage(" user with userID "+userID+" not existing in beehive");

	}
}
