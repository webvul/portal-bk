package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import com.kii.extension.sdk.entity.AppInfo;

public class KiiAppInfo {

	private AppInfo appInfo;


	private String appName;

	private Set<String> relThingIDs =new HashSet<String>();

	private String thingIDPrefix;

	private boolean isDefaultApp;

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

	public Set<String> getRelThingIDs() {
		return relThingIDs;
	}

	public void setRelThingIDs(Set<String> relThingIDs) {
		this.relThingIDs = relThingIDs;
	}

	public String getThingIDPrefix() {
		return thingIDPrefix;
	}

	public void setThingIDPrefix(String thingIDPrefix) {
		this.thingIDPrefix = thingIDPrefix;
	}

	public boolean isDefaultApp() {
		return isDefaultApp;
	}

	public void setIsDefaultApp(boolean isDefaultApp) {
		this.isDefaultApp = isDefaultApp;
	}
}
