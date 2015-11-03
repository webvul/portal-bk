package com.kii.extension.sdk.factory;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.SiteType;
import com.kii.extension.sdk.service.AppBindTool;

public class LocalPropertyBindTool implements AppBindTool {
	@Value("${kiicloud.master-app.appID}")
	private String appID;

	@Value("${kiicloud.master-app.appKey}")
	private String appKey;

	@Value("${kiicloud.master-app.clientID}")
	private String clientID;

	@Value("${kiicloud.master-app.secret}")
	private String secret;

	@Value("${kiicloud.master-app.site}")
	private String site;


	private AppInfo appInfo;

	@PostConstruct
	public void init(){

		appInfo=new AppInfo();
		appInfo.setClientSecret(secret);
		appInfo.setAppID(appID);
		appInfo.setAppKey(appKey);
		appInfo.setClientID(clientID);

		appInfo.setSite(SiteType.valueOf(site));
	}

	@Override
	public AppInfo getAppInfo() {
		return appInfo;
	}
}
