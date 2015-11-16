package com.kii.beehive.portal.store.entity;

import com.kii.extension.sdk.entity.KiiEntity;

public class NotifyUserFailure extends KiiEntity {

	private String party3rdID;

	private String urlNotifyUser;

	private String postContent;

	private String failureTime;


	public String getParty3rdID() {
		return party3rdID;
	}

	public void setParty3rdID(String party3rdID) {
		this.party3rdID = party3rdID;
	}

	public String getUrlNotifyUser() {
		return urlNotifyUser;
	}

	public void setUrlNotifyUser(String urlNotifyUser) {
		this.urlNotifyUser = urlNotifyUser;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public String getFailureTime() {
		return failureTime;
	}

	public void setFailureTime(String failureTime) {
		this.failureTime = failureTime;
	}
}
