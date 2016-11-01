package com.kii.extension.sdk.entity.thingif;

import org.apache.commons.lang3.StringUtils;

public class InstallationInfo {


	String  installationID;
	String  installationRegistrationID;
	String  installationType;
	String  deviceType;
	String  thingID;
	String  development;

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

	public String getInstallationType() {
		if(StringUtils.isNotBlank(installationType)) {
			return installationType;
		}else{
			return deviceType;
		}
	}

	public void setInstallationType(String installationType) {
		this.installationType = installationType;
	}

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
		this.thingID = thingID;
	}

	public String getDevelopment() {
		return development;
	}

	public void setDevelopment(String development) {
		this.development = development;
	}


	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
