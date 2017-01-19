package com.kii.beehive.portal.service;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.exception.BadUserNameException;
import com.kii.extension.sdk.service.UserService;

@Component
@BindAppByName(appName = "portal", appBindSource = "propAppBindTool",tokenBind = "admin")
public class PortalOperateUserService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppBindToolResolver bindToolResolver;
	
	
	
	@Value("${beehive.portal.store.defaultUser:operate_user}")
	private String userName;
	
	private String getPassword(String name){
		return DigestUtils.sha1Hex(bindToolResolver.getAppInfo().getAppID()+"_"+name);
	}
	
	
	
	
	
	private AtomicReference<LoginInfo> token=new AtomicReference<>();
	
	
	public void init(){
		
		
		
		try {
			
			LoginInfo  login=userService.login(userName, getPassword(userName));
			
			token.set(login);
			
		}catch(BadUserNameException ex){
			KiiUser user=new KiiUser();
			user.setDisplayName(" portal operate user");
			user.setLoginName(userName);
			user.setPassword(getPassword(userName));
			
			userService.createUser(user);
			
			LoginInfo info=userService.login(userName,getPassword(userName));
			
			token.set(info);
		}
	}
	
	public String getToken() {
		
//		return token.get().getToken();
		
		return "mock";
	}
	

	
}
