package com.kii.extension.ruleengine.sdk.context;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.sdk.service.UserService;
import com.kii.extension.ruleengine.sdk.entity.AppInfo;
import com.kii.extension.ruleengine.sdk.entity.LoginInfo;


@Component
public class UserTokenBindTool implements TokenBindTool {

	private Logger log= LoggerFactory.getLogger(UserTokenBindTool.class);

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

		log.debug("UserTokenBindTool.userLocal bind: " + appInfo.getAppID() + " / " + userInfo);
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

	/**
	 * clean the values in userLocal
	 */
	public void clean() {
		userLocal.remove();

		log.debug("UserTokenBindTool.userLocal clean");
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

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<userName=").append(userName);
			buffer.append(", password=").append(password);
			buffer.append(", token=").append(token);
			buffer.append(">");
			return buffer.toString();
		}
	}
}
