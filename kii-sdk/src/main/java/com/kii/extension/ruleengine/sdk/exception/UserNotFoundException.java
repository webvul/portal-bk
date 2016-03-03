package com.kii.extension.ruleengine.sdk.exception;

public class UserNotFoundException extends KiiCloudException {

/*
string	Error code USER_NOT_FOUND
message	string	The error message
field	string	The field used to search for the user, can be the userID field or an address field
value	string	The value of the field used to look for the user
appID	string	The appID of the application where the user belongs

 */
	@Override
	public int getStatusCode(){
	return 404;
}


	private String appID;

	private String field;

	private String value;

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
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
