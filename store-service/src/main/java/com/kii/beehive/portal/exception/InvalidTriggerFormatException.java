package com.kii.beehive.portal.exception;

import org.apache.http.HttpStatus;

public class InvalidTriggerFormatException  extends BusinessException{


	public InvalidTriggerFormatException(String reason){

		super.setErrorCode("INVALID_TRIGGER_FORMAT");

		super.setStatusCode(HttpStatus.SC_BAD_REQUEST);

		super.addParam("reason",reason);
	}

}
