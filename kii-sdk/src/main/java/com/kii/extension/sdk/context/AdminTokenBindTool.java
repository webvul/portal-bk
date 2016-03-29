package com.kii.extension.sdk.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.service.UserService;


@Component
public class AdminTokenBindTool implements TokenBindTool {

	private Logger log= LoggerFactory.getLogger(AdminTokenBindTool.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AppBindToolResolver bindToolResolver;



	private Map<String,LoginInfo> infoMap=new ConcurrentHashMap<>();


	@Scheduled(fixedRate=1000*60*60)
	public void clearCache(){

		log.info("clearCache");
		infoMap.clear();
	}


	@Override
	public String getToken() {

		AppInfo appInfo=bindToolResolver.getAppInfo();

		if(appInfo==null){
			log.debug("app is not bound while getting token");
			return null;
		}
		String appID=appInfo.getAppID();

		LoginInfo info= infoMap.computeIfAbsent(appID, (id)->userService.adminLogin());
		if(info==null){
			info= infoMap.get(appID);
		}

		String token = info.getToken();
		log.debug("got token " + token + " on app " + appID);

		return token;
	}
}
