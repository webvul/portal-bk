package com.kii.beehive.portal.exception;

public class TagFormatInvalidException extends StoreServiceException {

	public TagFormatInvalidException(String name){

		super.setErrorCode("TagRequiredFieldsMissing");

		super.setStatusCode(400);

		super.setMessage(name +" field miss  ");

	}
}
