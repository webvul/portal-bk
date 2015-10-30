package com.kii.extension.sdk.service;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.fxml.expression.Expression;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.AppInfoEntity;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.LoginInfo;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;
import com.kii.extension.sdk.query.QueryParam;

@Component
public class KiiCloudService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindTool bindTool;

	@Autowired
	private TokenBindTool  tool;

	private ApiAccessBuilder getBuilder(){
		AppInfo info= bindTool.getAppInfo();

		return new ApiAccessBuilder(info);
	}

	public LoginInfo login(String user,String pwd){

		HttpUriRequest  request=getBuilder().login(user, pwd).generRequest(mapper);


		return null;
//		return client.executeRequestWithCls(request,LoginInfo.class);


	}

	public LoginInfo adminLogin(){


		HttpUriRequest  request=getBuilder().adminLogin(bindTool.getAppInfo().getAppID(), bindTool.getAppInfo().getClientSecret()).generRequest(mapper);


		return client.executeRequestWithCls(request,LoginInfo.class);

	}

	public <T> T  getObjectByID(String id,BucketInfo bucket,Class<T> cls){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).getObjectByID(id).generRequest(mapper);

		return client.executeRequestWithCls(request,cls);

	}


	public <T> String  fullUpdateObject(String id,T obj,BucketInfo bucket){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).updateAll(id, obj).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);

		String version=response.getFirstHeader("ETag").getValue();


		return version;
	}

}
