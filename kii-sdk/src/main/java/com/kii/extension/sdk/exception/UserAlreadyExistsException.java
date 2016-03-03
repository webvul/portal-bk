package com.kii.extension.sdk.exception;

public class UserAlreadyExistsException extends KiiCloudException {

	/*
	Name	Type	Description
errorCode	string	Error code USER_ALREADY_EXISTS
message	string	The error message
field	string	The field used to check the user already exists, can be "loginName", "emailAddress" or "phoneNumber"
value	string	The value of the field
	 */

	private String errorCode;

	private String message;

	private String field;

	private String value;

	@Override
	public int getStatusCode(){
		return 409;
	}


	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

