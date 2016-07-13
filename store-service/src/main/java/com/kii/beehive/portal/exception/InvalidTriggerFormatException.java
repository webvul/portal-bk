package com.kii.beehive.portal.exception;

public class InvalidTriggerFormatException  extends BusinessException{


	public InvalidTriggerFormatException(String reason){
		super.addParam("reason",reason);
	}

}
