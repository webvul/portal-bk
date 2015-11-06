package com.kii.beehive.portal.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.helper.AppChoice;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.service.AppBindTool;

//@Component
public class DynamicAppInfoBindTool implements AppBindTool {

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private AppChoice appChoice;

	@Override
	public AppInfo getAppInfo() {

		return appInfoDao.getAppInfo(appChoice.getCurrAppID());
	}
}
