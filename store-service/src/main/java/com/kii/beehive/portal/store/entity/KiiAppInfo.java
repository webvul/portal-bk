package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.KiiEntity;

public class KiiAppInfo extends KiiEntity{

	private AppInfo appInfo;

	private String defaultThingOwnerID;

	private boolean isMasterApp=false;

	public boolean getMasterApp() {
		return isMasterApp;
	}

	@Override
	public String getId(){
		return appInfo.getAppID();
	}

	@Override
	public void setId(String id){

	}

	public void setMasterApp(boolean isMasterApp) {
		this.isMasterApp = isMasterApp;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public String getDefaultThingOwnerID() {
		return defaultThingOwnerID;
	}

	public void setDefaultThingOwnerID(String defaultThingOwnerID) {
		this.defaultThingOwnerID = defaultThingOwnerID;
	}
}
