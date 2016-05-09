package com.kii.beehive.portal.exception;


import org.apache.http.HttpStatus;

public class UserNotExistException extends BusinessException{


	public UserNotExistException(String userID){

		super.setErrorCode("BEEHIVE_USER_NOT_EXIST");

		super.setStatusCode(HttpStatus.SC_NOT_FOUND);

		super.addParam("userID",userID);
	}
}
