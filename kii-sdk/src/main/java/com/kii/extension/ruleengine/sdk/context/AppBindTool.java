package com.kii.extension.ruleengine.sdk.context;

import com.kii.extension.ruleengine.sdk.entity.AppInfo;

public interface AppBindTool {

	AppInfo getAppInfo(String appName);

	AppInfo getDefaultAppInfo();
}
