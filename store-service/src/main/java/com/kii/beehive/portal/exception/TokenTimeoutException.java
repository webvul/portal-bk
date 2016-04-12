package com.kii.beehive.portal.exception;

public class TokenTimeoutException extends StoreServiceException {


	public TokenTimeoutException(){
		super.setMessage("the token had time out");
		super.setErrorCode("TOKEN_TIME_OUT");
	}

	public int getStatusCode(){

		return 403;

	}
}
