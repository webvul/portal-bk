package com.kii.beehive.portal.store.entity;

import com.kii.extension.ruleengine.sdk.entity.AppInfo;
import com.kii.extension.ruleengine.sdk.entity.FederatedAuthResult;
import com.kii.extension.ruleengine.sdk.entity.KiiEntity;

public class KiiAppInfo extends KiiEntity{

	private AppInfo appInfo;

	private FederatedAuthResult federatedAuthResult;

	private String ownerToken;

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

	public String getOwnerToken() {
		return ownerToken;
	}

	public void setOwnerToken(String ownerToken) {
		this.ownerToken = ownerToken;
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

	public FederatedAuthResult getFederatedAuthResult() {
		return federatedAuthResult;
	}

	public void setFederatedAuthResult(FederatedAuthResult federatedAuthResult) {
		this.federatedAuthResult = federatedAuthResult;
	}
}
