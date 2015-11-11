package com.kii.extension.sdk.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.service.UserService;


@Component
public class AdminTokenBindTool implements TokenBindTool {

	@Autowired
	private UserService userService;

	@Autowired
	private AppBindToolResolver bindToolResolver;



	private Map<String,LoginInfo> infoMap=new ConcurrentHashMap<>();



	@Override
	public String getToken() {

		String appID=bindToolResolver.getAppInfo().getAppID();

		LoginInfo info= infoMap.putIfAbsent(appID, userService.adminLogin());
		if(info==null){
			info= infoMap.get(appID);
		}
		return info.getToken();
	}
}
