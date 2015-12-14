package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class BeehiveUserGroupRelation extends DBEntity{

	private long userID;

	private long userGroupID;

	public final static String ID = "id";
	public final static String USER_ID = "user_id";
	public final static String USER_GROUP_ID = "user_group_id";

	public BeehiveUserGroupRelation() {

	}

	public BeehiveUserGroupRelation(long userGroupID, long userID) {
		super();
		this.userGroupID = userGroupID;
		this.userID = userID;
	}

	@JdbcField(column=USER_ID)
	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	@JdbcField(column=USER_GROUP_ID)
	public long getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(long userGroupID) {
		this.userGroupID = userGroupID;
	}
}
