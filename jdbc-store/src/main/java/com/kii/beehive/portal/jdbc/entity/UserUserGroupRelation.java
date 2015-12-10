package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class UserUserGroupRelation extends DBEntity{

	private String userID;

	private String userGroupID;

	public final static String USER_ID = "user_id";
	public final static String USER_GROUP_ID = "user_group_id";

	@JdbcField(column=USER_ID)
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@JdbcField(column=USER_GROUP_ID)
	public String getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(String userGroupID) {
		this.userGroupID = userGroupID;
	}
}
