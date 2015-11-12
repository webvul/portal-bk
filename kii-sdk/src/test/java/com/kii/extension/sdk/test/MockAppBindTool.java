package com.kii.extension.sdk.test;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.SiteType;
import com.kii.extension.sdk.context.AppBindTool;

//@Component
public class MockAppBindTool implements AppBindTool {


	private AppInfo appInfo;

	public MockAppBindTool(){

		appInfo=new AppInfo();


		String appID="06e806e2";
		String appKey="31afdcdfd72ade025559176a40a20875";

		String client="9a08dd2cf74ef414e6a5ea8033e4a0a4";
		String secret="a82f1e144c111e11e4a9d62e93ef8c2a301e3efd6b6b1862ad4dac0a052a2d9e";


		appInfo.setAppID(appID);
		appInfo.setAppKey(appKey);
		appInfo.setClientID(client);
		appInfo.setClientSecret(secret);
		appInfo.setSiteType(SiteType.JP);

	}


	@Override
	public AppInfo getAppInfo(String appName) {
		return appInfo;
	}

	@Override
	public AppInfo getDefaultAppInfo() {
		return null;
	}
}
