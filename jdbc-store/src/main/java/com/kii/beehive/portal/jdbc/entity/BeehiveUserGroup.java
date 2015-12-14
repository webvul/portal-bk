package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class BeehiveUserGroup extends DBEntity{

	private String userGroupName;

	private String description;

	public final static String USER_GROUP_ID = "user_group_id";
	public final static String USER_GROUP_NAME = "user_group_name";
	public final static String DESCRIPTION = "description";


	@Override
	@JdbcField(column=USER_GROUP_ID)
	public long getId(){
		return super.getId();
	}

	@JdbcField(column=USER_GROUP_NAME)
	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	@JdbcField(column=DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
