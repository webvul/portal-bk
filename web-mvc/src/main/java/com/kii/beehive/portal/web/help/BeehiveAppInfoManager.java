package com.kii.beehive.portal.web.help;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.common.event.ListenerEnvInitService;
import com.kii.beehive.business.common.manager.AppInfoManager;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class BeehiveAppInfoManager {

	private Logger log= LoggerFactory.getLogger(BeehiveAppInfoManager.class);

	@Value("${beehive.kiicloud.dev-portal.username}")
	private String portalUserName;

	@Value("${beehive.kiicloud.dev-portal.password}")
	private String portalPwd;

	@Value("${beehive.kiicloud.dev-portal.masterApp}")
	private String masterAppID;


	@Autowired
	private ListenerEnvInitService eventInitService;

	@Autowired
	private AppInfoManager appManager;


	@Async
	public void initAllAppInfo(String userName,String pwd,String masterID,CallbackUrlParameter param){

		log.info("initAllAppInfo start");

		appManager.initAppInfos(userName,pwd,masterID);

		eventInitService.deployTriggerToAll(param);

		log.info("initAllAppInfo end");

	}

	@Async
	public void addAppInfo(String appID, CallbackUrlParameter param) {


		AppInfo appInfo=appManager.addAppInfo(appID,portalUserName,portalPwd,masterAppID);

		eventInitService.deployTrigger(appInfo,param);
	}
}
