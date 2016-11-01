package com.kii.extension.sdk.entity.thingif;

public class InstallationID {

	/*
	  "installationID" : "...",
  "installationRegistrationID" : "..."

	 */

	private String installationID;
	private String installationRegistrationID;

	public String getInstallationID() {
		return installationID;
	}

	public void setInstallationID(String installationID) {
		this.installationID = installationID;
	}

	public String getInstallationRegistrationID() {
		return installationRegistrationID;
	}

	public void setInstallationRegistrationID(String installationRegistrationID) {
		this.installationRegistrationID = installationRegistrationID;
	}
}
