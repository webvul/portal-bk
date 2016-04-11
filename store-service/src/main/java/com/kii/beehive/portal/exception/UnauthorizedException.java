package com.kii.beehive.portal.exception;

/**
 * Created by hdchen on 3/24/16.
 */
public class UnauthorizedException extends StoreServiceException {

	public UnauthorizedException(String msg){
		super.setMessage(msg);
	}

	public int getStatusCode(){

		return 403;

	}
}
