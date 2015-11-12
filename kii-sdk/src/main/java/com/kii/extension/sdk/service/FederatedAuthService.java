package com.kii.extension.sdk.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class FederatedAuthService {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;

	private String authUrl="https://$(0).$(1).kii.com/api/apps/$(0)/integration/webauth/connect?id=kii";
	public void getAuthUrl(String appName){

		/*
GET https://<slaveAppId>.<kiiapps-domain>/api/apps/<slaveAppId>/integration/webauth/connect?id=kii
		 */

//		AppInfo info=bindToolResolver.getAppInfoByName(appName);
//
//		String fullUrl= StrTemplate.generUrl(authUrl, info.getAppID(), info.getSiteType().getSite());
//
//		HttpUriRequest request=new HttpGet(fullUrl);
//
//		HttpResponse response=client.doRequest(request);
//
//		int code=response.getStatusLine().getStatusCode();

	}
}
