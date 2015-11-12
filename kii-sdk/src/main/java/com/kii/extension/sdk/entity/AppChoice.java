package com.kii.extension.sdk.entity;

import java.io.Serializable;

import org.springframework.util.StringUtils;

public class AppChoice implements Serializable{

	private String bindName;

	private String appName;

	public String getBindName() {
		return bindName;
	}

	public void setBindName(String bindName) {
		this.bindName = bindName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		if(!StringUtils.isEmpty(appName)) {
			this.appName = appName;
		}
	}
}
