package com.kii.beehive.business.ruleengine;

import org.apache.http.HttpStatus;

public class TriggerServiceException extends TriggerException {
	
	
	public TriggerServiceException(Exception exception) {

		super.setErrorCode("TRIGGER_SERICE_INVALID");

		super.setStatusCode(HttpStatus.SC_BAD_REQUEST);

		super.addParam("reason",exception.getMessage());
	}

}
