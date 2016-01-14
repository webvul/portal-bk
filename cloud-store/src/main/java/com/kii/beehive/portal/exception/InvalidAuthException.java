package com.kii.beehive.portal.exception;


public class InvalidAuthException extends StoreServiceException {


	public InvalidAuthException(String userID,String owner){

		super.setErrorCode("AuthNoRight");

		super.setStatusCode(401);

		super.setMessage(" curr user "+userID+" no right to access "+owner+"'s data");



	}
}
