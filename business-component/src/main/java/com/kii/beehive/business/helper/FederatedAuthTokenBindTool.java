package com.kii.beehive.business.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindTool;

@Component
public class FederatedAuthTokenBindTool implements TokenBindTool {


	public  static final String FEDERATED = "federatedDefaultUser";

	@Autowired
	private AppBindToolResolver resolver;


	@Autowired
	private SlaveAppDefaultTokenBind  slaveTokenBind;

	public String getToken() {
		
		return slaveTokenBind.getToken(resolver.getAppInfo().getAppID());
		

	}

	@Override
	public String getBindName() {
		return FEDERATED;
	}

	@Override
	public void refreshToken() {
		//TODO:add slave token get logic
//		slaveTokenBind.refreshSlaveToken(resolver.getAppInfo().getAppID());
		
	}


	static class UserInfo {

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
