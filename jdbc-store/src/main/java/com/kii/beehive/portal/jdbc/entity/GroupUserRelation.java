package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class GroupUserRelation extends DBEntity{

	private Long id;
	
	private String userID;

	private Long userGroupID;

	private Long beehiveUserID;

	
	public final static String ID = "id";
	public final static String USER_ID = "beehive_user_id";
	public final static String USER_GROUP_ID = "user_group_id";
	final public static String OLD_USER_ID = "user_id";

	
	public GroupUserRelation() {}
	
	public GroupUserRelation(String userID, Long userGroupID) {
		super();
		this.userID = userID;
		this.userGroupID = userGroupID;
	}

	@JdbcField(column=USER_ID)
	public Long getBeehiveUserID() {
		return beehiveUserID;
	}

	public void setBeehiveUserID(Long beehiveUserID) {
		this.beehiveUserID = beehiveUserID;
	}

	@JdbcField(column=ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JdbcField(column=USER_GROUP_ID)
	public Long getUserGroupID() {
		return userGroupID;
	}
	
	public void setUserGroupID(Long userGroupID) {
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
