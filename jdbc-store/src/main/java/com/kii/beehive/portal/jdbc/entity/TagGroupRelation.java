package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class TagGroupRelation extends DBEntity{

	private Long id;
	
	private Long tagID;

	private Long userGroupID;
	
	private String type;
	
	public final static String ID = "id";
	public final static String TAG_ID = "tag_id";
	public final static String USER_GROUP_ID = "user_group_id";
	public final static String TYPE = "type";
	
	public TagGroupRelation() {}
	

	@JdbcField(column=ID)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@JdbcField(column = TAG_ID)
	public Long getTagID() {
		return tagID;
	}

	public void setTagID(Long tagID) {
		this.tagID = tagID;
	}

	@JdbcField(column=USER_GROUP_ID)
	public Long getUserGroupID() {
		return userGroupID;
	}
	
	public void setUserGroupID(Long userGroupID) {
		this.userGroupID = userGroupID;
	}
}
