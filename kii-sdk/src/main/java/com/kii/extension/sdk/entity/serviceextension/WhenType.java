package com.kii.extension.sdk.entity.serviceextension;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


public enum WhenType {

	USER_CREATED,USER_EMAIL_VERIFIED,USER_PHONE_VERIFIED,USER_PASSWORD_RESET_COMPLETED,
	USER_PASSWORD_CHANGED,USER_DELETED,USER_UPDATED,

	THING_CREATED,THING_ENABLED,THING_DISABLED,THING_USER_OWNER_ADDED,
	THING_GROUP_OWNER_ADDED,THING_USER_OWNER_REMOVED,THING_GROUP_OWNER_REMOVED,
	THING_FIELDS_UPDATED,THING_DELETED,THING_CONNECTED,THING_DISCONNECTED,

	GROUP_CREATED,
	GROUP_DELETED,
	GROUP_MEMBERS_ADDED,
	GROUP_MEMBERS_REMOVED,

	DATA_OBJECT_CREATED,
	DATA_OBJECT_DELETED,
	DATA_OBJECT_UPDATED;

}
