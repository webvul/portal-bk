package com.kii.beehive.portal.exception;

/**
 * Created by hdchen on 3/23/16.
 */
public class ObjectNotFoundException extends StoreServiceException {


	public ObjectNotFoundException(String msg){
		super.setMessage(msg);
	}

	public String getErrorMsg(){

		return "object not found";
	}

	public int getStatusCode(){

		return 404;

	}
}
