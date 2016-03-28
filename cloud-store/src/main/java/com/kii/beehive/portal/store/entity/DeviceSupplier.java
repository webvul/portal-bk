package com.kii.beehive.portal.store.entity;

public class DeviceSupplier extends PortalEntity {


	private String name;

	private String relationAppName;

	private String relationAppID;

	private String description;

	private String userInfoNotifyUrl="";


	public String getParty3rdID() {
		return getId();
	}

	public void setParty3rdID(String party3rdID) {
		setId(party3rdID);
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

	public String getRelationAppID() {
		return relationAppID;
	}

	public void setRelationAppID(String relationAppID) {
		this.relationAppID = relationAppID;
	}
}
