package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import com.kii.extension.sdk.entity.AppInfo;

public class KiiAppInfo {

	private AppInfo appInfo;


	private String appName;

	private Set<String> thingIDs=new HashSet<String>();

	private String thingIDPrefix;

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Set<String> getThingIDs() {
		return thingIDs;
	}

	public void setThingIDs(Set<String> thingIDs) {
		this.thingIDs = thingIDs;
	}

	public String getThingIDPrefix() {
		return thingIDPrefix;
	}

	public void setThingIDPrefix(String thingIDPrefix) {
		this.thingIDPrefix = thingIDPrefix;
	}
}
