package com.kii.beehive.portal.store.entity;

import com.kii.extension.sdk.entity.KiiEntity;

public class DeviceSupplier extends KiiEntity {


	private String name;

	private String type;

	private String relationAppName;

	private String description;

	private String userInfoNotifyUrl;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelationAppName() {
		return relationAppName;
	}

	public void setRelationAppName(String relationAppName) {
		this.relationAppName = relationAppName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserInfoNotifyUrl() {
		return userInfoNotifyUrl;
	}

	public void setUserInfoNotifyUrl(String userInfoNotifyUrl) {
		this.userInfoNotifyUrl = userInfoNotifyUrl;
	}
}
