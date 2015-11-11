package com.kii.extension.factory;

import javax.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.context.AppBindTool;
import com.kii.extension.sdk.service.DevPortalService;

public class DevPortalBindTool implements AppBindTool {

	private String userName;

	private String password;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Autowired
	private DevPortalService portalService;

	private Map<String,AppInfo> appInfoMap=new ConcurrentHashMap<>();


	@PostConstruct
	public void init(){

		portalService.login(userName,password);


		portalService.getAppInfoList().forEach(info->{

			appInfoMap.put(info.getName(),info);

		});



	}

	@Override
	public AppInfo getAppInfo(String appName) {

		AppInfo info=appInfoMap.get(appName);

		if(info==null){
			init();
		}
		return appInfoMap.get(appName);
	}


	@Override
	public AppInfo getDefaultAppInfo() {
		return null;
	}
}
