package com.kii.beehive.portal.web.help;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.manager.AppInfoManager;
import com.kii.beehive.portal.manager.TriggerMaintainManager;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class BeehiveAppInfoManager {



	@Value("${beehive.kiicloud.dev-portal.username}")
	private String portalUserName;

	@Value("${beehive.kiicloud.dev-portal.password}")
	private String portalPwd;

	@Value("${beehive.kiicloud.dev-portal.masterApp}")
	private String masterAppID;


	@Autowired
	private TriggerMaintainManager maintainManager;


	@Autowired
	private AppInfoManager appManager;


	@Async
	public void initAllAppInfo(String userName,String pwd,String masterID,CallbackUrlParameter param){

		appManager.initAppInfos(userName,pwd,masterID);

		maintainManager.deployTriggerToAll(param);

	}

	@Async
	public void addAppInfo(String appID, CallbackUrlParameter param) {


		AppInfo appInfo=appManager.addAppInfo(appID,portalUserName,portalPwd,masterAppID);

		maintainManager.deployTrigger(appInfo,param);
	}
}
