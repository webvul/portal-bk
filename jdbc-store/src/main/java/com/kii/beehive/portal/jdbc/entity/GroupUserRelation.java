package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class GroupUserRelation extends DBEntity {

	private long id;
	
	private String userID;

	private long userGroupID;
	
	public final static String ID = "id";
	public final static String USER_ID = "user_id";
	public final static String USER_GROUP_ID = "user_group_id";
	
	public GroupUserRelation() {}
	

	@JdbcField(column=ID)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JdbcField(column=USER_GROUP_ID)
	public long getUserGroupID() {
		return userGroupID;
	}
	
	public void setUserGroupID(long userGroupID) {
		this.userGroupID = userGroupID;
	}

	@JdbcField(column=USER_ID)
	public String getUserID() {
		return userID;
	}


	public void setUserID(String userID) {
		this.userID = userID;
	}
}
