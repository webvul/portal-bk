package com.kii.extension.ruleengine.sdk.exception;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class ObjectScope {

	/*
	appID	string	The application ID.
userID	string	The user ID. Only provided if it is a user scope.
groupID	string	The group ID. Only provided if it is a group scope.
thingID	string	The thing ID. Only provided if it is a thing scope.
type	string	One of "APP", "APP_AND_USER", "APP_AND_GROUP" or "APP_AND_THING".

	 */

	private String scopeID;

	private ObjectScopeType type;

	public String getScopeID() {
		return scopeID;
	}


	@JsonAnySetter
	public void setScopeID(String scopeID) {
		this.scopeID = scopeID;
	}


	public ObjectScopeType getType() {
		return type;
	}

	public void setType(ObjectScopeType type) {
		this.type = type;
	}
}
