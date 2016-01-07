package com.kii.extension.sdk.service;

import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class UserService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;

	@Autowired
	private TokenBindToolResolver tool;

	public  LoginInfo login(String userName,String password){


		HttpUriRequest request= getNonTokenBuilder().login(userName, password).generRequest(mapper);

		LoginInfo login= client.executeRequestWithCls(request, LoginInfo.class);

		return login;
	}

	public  LoginInfo loginWithCode(String code,String clientID){


		HttpUriRequest request= getNonTokenBuilder().loginWithCode(code, clientID).setContentType("application/x-www-form-urlencoded").generRequest(mapper);

		LoginInfo login= client.executeRequestWithCls(request, LoginInfo.class);

		return login;
	}

	private ApiAccessBuilder getBuilder() {
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info).bindToken(bindToolResolver.getToken());
	}


	private ApiAccessBuilder getNonTokenBuilder(){
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info);

	}
	public LoginInfo  adminLogin(){

		AppInfo appInfo=bindToolResolver.getAppInfo();

		HttpUriRequest request= getNonTokenBuilder().adminLogin(appInfo.getClientID(), appInfo.getClientSecret()).generRequest(mapper);


		LoginInfo login= client.executeRequestWithCls(request, LoginInfo.class);

		return login;
	}

	public String createUser(KiiUser user){

		HttpUriRequest request=getNonTokenBuilder().createUser(user).generRequest(mapper);

		Map<String,String> map=client.executeRequestWithCls(request,Map.class);

		return map.get("userID");

	}

	public void changePassword(String oldPassword, String newPassword) {

		// use user token for change password
		tool.bindUser();

		HttpUriRequest request=getBuilder().changePassword(oldPassword, newPassword).generRequest(mapper);

		client.doRequest(request);

	}

	public void disableUser(String kiiUserID) {

		HttpUriRequest request=getBuilder().setUserStatus(kiiUserID,true).generRequest(mapper);

		client.doRequest(request);
	}

	public void enableUser(String kiiUserID) {
		HttpUriRequest request=getBuilder().setUserStatus(kiiUserID,false).generRequest(mapper);

		client.doRequest(request);

	}

	public void removeUserByLoginName(String beehiveUserID) {

		HttpUriRequest request=getBuilder().deleteUser(beehiveUserID, "LOGIN_NAME").generRequest(mapper);

		client.doRequest(request);
	}


	public void removeUserByID(String beehiveUserID) {

		HttpUriRequest request=getBuilder().deleteUser(beehiveUserID,null).generRequest(mapper);

		client.doRequest(request);
	}


}
