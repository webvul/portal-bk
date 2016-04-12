package com.kii.extension.sdk.entity;

import java.io.Serializable;

import org.springframework.util.StringUtils;

public class AppChoice implements Serializable{

	private String bindName;

	private String appName;

	private boolean bindAdmin;


	public boolean isBindAdmin() {
		return bindAdmin;
	}

	public void setBindAdmin(boolean bindAdmin) {
		this.bindAdmin = bindAdmin;
	}

	public String getBindName() {
		return bindName;
	}

	public AppChoice setBindName(String bindName) {
		this.bindName = bindName;
		return this;
	}

	public String getAppName() {
		return appName;
	}

	public AppChoice setAppName(String appName) {
		if(!StringUtils.isEmpty(appName)) {
			this.appName = appName;
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppChoice [bindName=");
		builder.append(bindName);
		builder.append(", appName=");
		builder.append(appName);
		builder.append("]");
		return builder.toString();
	}
}
