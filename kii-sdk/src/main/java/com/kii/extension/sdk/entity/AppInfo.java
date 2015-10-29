package com.kii.extension.sdk.entity;


public class AppInfo {

	private String name;

	private String appID;

	private String appKey;

	private SiteType site;

	private String clientID;

	private String clientSecret;


	public AppInfo(){

	}

	public AppInfo(AppInfo appInfo) {
		this.name=appInfo.getName();
		this.appID=appInfo.getAppID();
		this.appKey=appInfo.getAppKey();
		this.clientID=appInfo.getClientID();
		this.clientSecret=appInfo.getClientSecret();
		this.site=appInfo.site;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public SiteType getSite() {
		return site;
	}

	public void setSite(SiteType site) {
		this.site = site;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getSiteUrl(String subUrl) {
		return site.getSiteUrl()+"/api/apps/"+appID+"/"+subUrl;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSubUrl() {

		return  site.getSiteUrl()+"/api/apps";
	}
}
