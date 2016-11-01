package com.kii.extension.sdk.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MQTTNotReadyException extends KiiCloudException {


	@JsonIgnore
	public int getStatusCode() {
		return 503;
	}

	/*
	  "errorCode" : "MQTT_ENDPOINT_NOT_READY",
  "message" : "MQTT endpoint for  hkh6ad3ui75wcoyhueif5w366 is not ready. Please try again later.",
  "installationID" : "hkh6ad3ui75wcoyhueif5w366",
  "appID" : "ulQ6WRjBbySBHw2I4MVc",

	 */

	private String installationID;

	private String appID;

	public String getInstallationID() {
		return installationID;
	}

	public void setInstallationID(String installationID) {
		this.installationID = installationID;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}
}
