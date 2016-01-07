package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class GroupPermissionRelation extends DBEntity {

	private Long id;
	
	private Long permissionID;

	private Long userGroupID;
	
	public final static String ID = "id";
	public final static String PERMISSION_ID = "permission_id";
	public final static String USER_GROUP_ID = "user_group_id";
	
	public GroupPermissionRelation() {}
	

	@JdbcField(column=ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JdbcField(column=PERMISSION_ID)
	public Long getPermissionID() {
		return permissionID;
	}

	public void setPermissionID(Long permissionID) {
		this.permissionID = permissionID;
	}

	@JdbcField(column=USER_GROUP_ID)
	public long getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(Long userGroupID) {
		this.userGroupID = userGroupID;
	}
	
	
}
