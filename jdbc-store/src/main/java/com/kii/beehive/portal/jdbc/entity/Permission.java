package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class Permission extends DBEntity {

	private long sourceID;
	private String name;
	private String action;
	private String description;
	
	public final static String PERMISSION_ID = "permission_id";
	public final static String SOURCE_ID = "source_id";
	public final static String NAME = "name";
	public final static String ACTION = "action";
	public final static String DESCRIPTION = "description";
	
	@Override
	@JdbcField(column=PERMISSION_ID)
	public long getId(){
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

	@JdbcField(column=SOURCE_ID)
	public long getSourceID() {
		return sourceID;
	}

	public void setSourceID(long sourceID) {
		this.sourceID = sourceID;
	}

	@JdbcField(column=ACTION)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
}
