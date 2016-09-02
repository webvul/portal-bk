package com.kii.beehive.portal.store.entity;

public class UserGeneratedContent extends PortalEntity {



	private Long userID;

	private String  dataType;

	private String name;

	private CustomData data;


	public static  String getUUID(Long userID,String type,String name){

		return "UGC-"+type+"-"+name+"-"+userID;

	}

	public CustomData getUserData() {
		return data;
	}

	public void setUserData(CustomData data) {
		this.data = data;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}


	public String getUserDataType() {
		return dataType;
	}

	public void setUserDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
