package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class GroupPermissionRelation extends DBEntity {

	private long id;
	
	private long permissionID;

	private long userGroupID;
	
	public final static String ID = "id";
	public final static String PERMISSION_ID = "permission_id";
	public final static String USER_GROUP_ID = "user_group_id";
	
	public GroupPermissionRelation() {}
	

	@JdbcField(column=ID)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JdbcField(column=PERMISSION_ID)
	public long getPermissionID() {
		return permissionID;
	}

	public void setPermissionID(long permissionID) {
		this.permissionID = permissionID;
	}

	@JdbcField(column=USER_GROUP_ID)
	public long getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(long userGroupID) {
		this.userGroupID = userGroupID;
	}
	
	
}
