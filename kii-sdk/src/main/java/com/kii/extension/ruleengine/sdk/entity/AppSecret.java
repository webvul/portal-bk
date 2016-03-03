package com.kii.extension.ruleengine.sdk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppSecret {


	private String clientID;

	private String clientSecret;

	public String getClientID() {
		return clientID;
	}

	@JsonProperty("client_id")
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	@JsonProperty("client_secret")
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void fillAppInfo(AppInfo info){

		info.setClientID(clientID);
		info.setClientSecret(clientSecret);
	}
}
