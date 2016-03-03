package com.kii.extension.ruleengine.sdk.entity;



public class AppInfo {

	private String name;

	private String appID;

	private String appKey;

	private String  siteUrl;

	private String clientID;

	private String clientSecret;

	private SiteType site;



	public AppInfo(){

	}



	public AppInfo(AppInfo appInfo) {
		this.name=appInfo.getName();
		this.appID=appInfo.getAppID();
		this.appKey=appInfo.getAppKey();
		this.clientID=appInfo.getClientID();
		this.clientSecret=appInfo.getClientSecret();
		this.site=appInfo.site;
		this.siteUrl=appInfo.siteUrl;

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

//	public SiteType getSite() {
//		return site;
//	}

	public void setSiteType(SiteType site) {

		this.site = site;
		if(siteUrl==null) {
			this.siteUrl = site.getSiteUrl();
		}
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
		return getAppSubUrl()+"/"+subUrl;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSubUrl() {

		return  siteUrl+"/api/apps/"+appID;
	}

	public String getThingIfSubUrl(){
		return siteUrl+"/thing-if/apps/"+appID;
	}

	public String getSiteUrl(){
		return siteUrl;
	}

	public SiteType getSiteType(){
		return site;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
}
