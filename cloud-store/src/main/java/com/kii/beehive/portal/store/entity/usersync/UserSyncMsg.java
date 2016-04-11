package com.kii.beehive.portal.store.entity.usersync;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.store.entity.PortalSyncUser;


public class UserSyncMsg {

	private UserSyncMsgType type;

	private PortalSyncUser user;

	private String userID;

	private int retryCount=0;



	@JsonIgnore()
	public PortalSyncUser getUser() {
		return user;
	}

	public void setUser(PortalSyncUser user) {
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
