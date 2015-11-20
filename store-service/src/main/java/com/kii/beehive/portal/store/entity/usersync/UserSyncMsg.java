package com.kii.beehive.portal.store.entity.usersync;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.store.entity.BeehiveUser;

public class UserSyncMsg {

	private UserSyncMsgType type;

	private BeehiveUser user;

//	private Map<String,Object> userMap;

	private String userID;

	@JsonProperty("context")
	public BeehiveUser getUser() {
		return user;
	}

	public void setUser(BeehiveUser user) {
		this.user = user;
	}

//	@JsonProperty("context")
//	public Map<String, Object> getUserMap() {
//		return userMap;
//	}
//
//	public void setUserMap(Map<String, Object> userMap) {
//		this.userMap = userMap;
//	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public UserSyncMsgType getType() {
		return type;
	}

	public void setType(UserSyncMsgType type) {
		this.type = type;
	}


}
