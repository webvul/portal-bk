package com.kii.beehive.portal.store.entity.usersync;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.store.entity.BeehiveUser;

public class UserSyncMsg {

	private UserSyncMsgType type;

	private BeehiveUser user;

	private String userID;

	private int retryCount=0;



	@JsonIgnore()
	public BeehiveUser getUser() {
		return user;
	}

	public void setUser(BeehiveUser user) {
		this.user = user;
	}

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

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
}
