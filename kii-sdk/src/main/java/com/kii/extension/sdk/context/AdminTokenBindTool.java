package com.kii.extension.sdk.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.service.UserService;


@Component
public class AdminTokenBindTool implements TokenBindTool {

	@Autowired
	private UserService userService;

	@Autowired
	private AppBindToolResolver bindToolResolver;



	private Map<String,LoginInfo> infoMap=new ConcurrentHashMap<>();


	@Scheduled(fixedRate=1000*60*60)
	public void clearCache(){

		infoMap.clear();
	}


	@Override
	public String getToken() {

		AppInfo appInfo=bindToolResolver.getAppInfo();

		if(appInfo==null){
			return null;
		}
		String appID=appInfo.getAppID();

		LoginInfo info= infoMap.computeIfAbsent(appID, (id)->userService.adminLogin());
		if(info==null){
			info= infoMap.get(appID);
		}
		return info.getToken();
	}
}
