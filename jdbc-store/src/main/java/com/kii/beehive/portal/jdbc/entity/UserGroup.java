package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class UserGroup extends DBEntity {

	private String name;
	private String description;
	
	public final static String USER_GROUP_ID = "user_group_id";
	public final static String NAME = "name";
	public final static String DESCRIPTION = "description";
	
	@Override
	@JdbcField(column=USER_GROUP_ID)
	public Long getId(){
		return super.getId();
	}
	
	@JdbcField(column=NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JdbcField(column=DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
