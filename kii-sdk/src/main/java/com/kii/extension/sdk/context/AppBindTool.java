package com.kii.extension.sdk.context;

import com.kii.extension.sdk.entity.AppInfo;

public interface AppBindTool {

	AppInfo getAppInfo(String appName);

	AppInfo getDefaultAppInfo();
}
