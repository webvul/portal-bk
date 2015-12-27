package com.kii.extension.sdk.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.service.UserService;


@Component
public class UserTokenBindTool implements TokenBindTool {

	@Autowired
	private UserService userService;

	@Autowired
	private AppBindToolResolver bindToolResolver;
//
//	@Autowired
//	private ObjectMapper mapper;


	private ThreadLocal<Map<String,UserInfo>> userLocal=ThreadLocal.withInitial(()->new HashMap<>());


	private void setUserInfo(UserInfo userInfo){

		AppInfo appInfo =  bindToolResolver.getAppInfo();

		userLocal.get().put(appInfo.getAppID(), userInfo);
	}


	public void bindUserInfo(String userName,String password){

		UserInfo user=new UserInfo();
		user.setUserName(userName);
		user.setPassword(password);

		LoginInfo login=userService.login(userName,password);

		user.setToken(login.getToken());

		setUserInfo(user);
	}

	public void bindToken(String token){
		UserInfo user=new UserInfo();
		user.setToken(token);

		setUserInfo(user);
	}


	@Override
	public String getToken() {

		AppInfo appInfo = bindToolResolver.getAppInfo();

		UserInfo info=userLocal.get().get(appInfo.getAppID());
		if(info==null){
			return null;
		}

		return info.getToken();
	}


	public static class UserInfo{

		private String userName;

		private String password;

		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
