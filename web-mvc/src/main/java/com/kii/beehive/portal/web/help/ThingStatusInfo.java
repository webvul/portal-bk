package com.kii.beehive.portal.web.help;

import com.kii.beehive.portal.web.entity.StateUpload;

/**
 * Created by hdchen on 7/7/16.
 */
public class ThingStatusInfo {
	private String appId;

	private StateUpload status;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public StateUpload getStatus() {
		return status;
	}

	public void setStatus(StateUpload status) {
		this.status = status;
	}
}
