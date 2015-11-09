package com.kii.extension.sdk.service;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.AppInfoEntity;

public interface AppBindTool {

	AppInfo getAppInfo(String appName);

	AppInfo getDefaultAppInfo();
}
