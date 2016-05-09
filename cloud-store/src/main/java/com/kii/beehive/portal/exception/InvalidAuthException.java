package com.kii.beehive.portal.exception;


public class InvalidAuthException extends BusinessException {


	public InvalidAuthException(String userID,String owner){

		super.setErrorCode("AUTH_NO_RIGHT");

		super.setStatusCode(401);

		super.addParam("userID",userID);
		super.addParam("owner",owner);

	}

}
