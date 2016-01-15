package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class Permission extends DBEntity {

	private Long sourceID;
	private String name;
	private String action;
	private String description;
	private String sourceName;
	
	public final static String PERMISSION_ID = "permission_id";
	public final static String SOURCE_ID = "source_id";
	public final static String NAME = "name";
	public final static String ACTION = "action";
	public final static String DESCRIPTION = "description";
	public final static String SOURCE_NAME = "sourceName";
	
	@Override
	@JdbcField(column=PERMISSION_ID)
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

	@JdbcField(column=SOURCE_ID)
	public Long getSourceID() {
		return sourceID;
	}

	public void setSourceID(Long sourceID) {
		this.sourceID = sourceID;
	}

	@JdbcField(column=ACTION)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Permission [sourceID=");
		builder.append(sourceID);
		builder.append(", name=");
		builder.append(name);
		builder.append(", action=");
		builder.append(action);
		builder.append(", description=");
		builder.append(description);
		builder.append(", sourceName=");
		builder.append(sourceName);
		builder.append("]");
		return builder.toString();
	}
	
	
}
