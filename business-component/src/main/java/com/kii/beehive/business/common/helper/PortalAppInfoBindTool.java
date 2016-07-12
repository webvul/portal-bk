package com.kii.beehive.business.common.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.context.AppBindTool;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class PortalAppInfoBindTool implements AppBindTool {

	@Autowired
	private AppInfoDao appDao;


	@Override
	public AppInfo getAppInfo(String appName) {

		if(appName.equals("master")){
			return appDao.getMasterAppInfo().getAppInfo();
		}else{

			KiiAppInfo kiiAppInfo=appDao.getAppInfoByID(appName);
			if(kiiAppInfo==null){
				return null;
			}else{
				return kiiAppInfo.getAppInfo();
			}
		}
	}

	@Override
	public AppInfo getDefaultAppInfo() {
		return appDao.getMasterAppInfo().getAppInfo();
	}
}
