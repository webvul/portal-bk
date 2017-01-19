//package com.kii.beehive.business.helper;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.extension.sdk.context.AppBindToolResolver;
//import com.kii.extension.sdk.context.TokenBindTool;
//import com.kii.extension.sdk.service.FederatedAuthService;
//
//public class FederatedAuthTokenBindTool implements TokenBindTool {
//
//
//	@Autowired
//	private AppBindToolResolver resolver;
//
//	@Autowired
//	private FederatedAuthService  authService;
//
//
//	private ThreadLocal<UserInfo>  idLocal=new ThreadLocal<>();
//
//	public void bindUserInfo(String userName,String password){
//
//		UserInfo user=new UserInfo();
//		user.setUserName(userName);
//		user.setPassword(password);
//
//		idLocal.set(user);
//	}
//
//	public void bindToken(String token){
//		UserInfo user=new UserInfo();
//		user.setToken(token);
//
//		idLocal.set(user);
//	}
//
//
//	public String getToken() {
//
//
//		UserInfo info=idLocal.get();
//		if(info==null){
//			return null;
//		}
//		if(info.getToken()==null){
//
//			String userName=info.getUserName();
//			String password=info.getPassword();
//
//			return null;
//		}
//
//		return info.getToken();
//	}
//
//
//	static class UserInfo {
//
//		private String userName;
//
//		private String password;
//
//		private String token;
//
//		public String getToken() {
//			return token;
//		}
//
//		public void setToken(String token) {
//			this.token = token;
//		}
//
//		public String getUserName() {
//			return userName;
//		}
//
//		public void setUserName(String userName) {
//			this.userName = userName;
//		}
//
//		public String getPassword() {
//			return password;
//		}
//
//		public void setPassword(String password) {
//			this.password = password;
//		}
//
//
//	}
//}
