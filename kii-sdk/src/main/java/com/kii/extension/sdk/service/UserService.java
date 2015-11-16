package com.kii.extension.sdk.service;

import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.context.AppBindToolResolver;
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

	public  LoginInfo login(String userName,String password){


		HttpUriRequest request= getBuilder().login(userName, password).generRequest(mapper);

		LoginInfo login= client.executeRequestWithCls(request, LoginInfo.class);

		return login;
	}

	public  LoginInfo loginWithCode(String code,String clientID){


		HttpUriRequest request= getBuilder().loginWithCode(code, clientID).setContentType("application/x-www-form-urlencoded").generRequest(mapper);

		LoginInfo login= client.executeRequestWithCls(request, LoginInfo.class);

		return login;
	}

	private ApiAccessBuilder getBuilder() {
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info);
	}

	public LoginInfo  adminLogin(){


		HttpUriRequest request= getBuilder().adminLogin(bindToolResolver.getAppInfo().getClientID(), bindToolResolver.getAppInfo().getClientSecret()).generRequest(mapper);


		LoginInfo login= client.executeRequestWithCls(request, LoginInfo.class);

		return login;
	}

	public String createUser(KiiUser user){

		HttpUriRequest request=getBuilder().createUser(user).generRequest(mapper);

		Map<String,String> map=client.executeRequestWithCls(request,Map.class);

		return map.get("userID");

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
