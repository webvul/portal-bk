package com.kii.beehive.portal.exception;

public class TokenTimeoutException extends BusinessException {


	public TokenTimeoutException(String token){
		super.setErrorCode("TOKEN_TIME_OUT");

		super.addParam("token",token);
	}

	public int getStatusCode(){

		return 403;

	}
}
