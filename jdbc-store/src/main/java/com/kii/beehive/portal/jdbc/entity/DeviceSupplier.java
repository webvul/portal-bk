package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class DeviceSupplier extends DBEntity {

	private String name;

	private String relationAppName;

	private String description;

	private String userInfoNotifyUrl;

	public final static String PARTY_3RD_ID = "part3rd_id";
	public final static String NAME = "name";
	public final static String RELATION_APP_NAME = "relation_app_name";
	public final static String DESCRIPTION = "description";
	public final static String USER_INFO_NOTIFY_URL = "user_info_notify_url";

	@Override
	@JdbcField(column=PARTY_3RD_ID)
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

	@JdbcField(column=RELATION_APP_NAME)
	public String getRelationAppName() {
		return relationAppName;
	}

	public void setRelationAppName(String relationAppName) {
		this.relationAppName = relationAppName;
	}

	@JdbcField(column=DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JdbcField(column=USER_INFO_NOTIFY_URL)
	public String getUserInfoNotifyUrl() {
		return userInfoNotifyUrl;
	}

	public void setUserInfoNotifyUrl(String userInfoNotifyUrl) {
		this.userInfoNotifyUrl = userInfoNotifyUrl;
	}

}
