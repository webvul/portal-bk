package com.kii.extension.sdk.service;

import javax.annotation.PostConstruct;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;


public class AdminTokenBindTool implements TokenBindTool {

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindTool bindTool;

	@Autowired
	private ObjectMapper mapper;

	private ApiAccessBuilder getBuilder(){
		AppInfo info= bindTool.getAppInfo();

		return new ApiAccessBuilder(info);
	}

	private LoginInfo info;

	@PostConstruct
	public void  adminLogin(){


		HttpUriRequest request=getBuilder().adminLogin(bindTool.getAppInfo().getClientID(), bindTool.getAppInfo().getClientSecret()).generRequest(mapper);


		info= client.executeRequestWithCls(request, LoginInfo.class);
	}

	@Override
	public String getToken() {
		return info.getToken();
	}
}
