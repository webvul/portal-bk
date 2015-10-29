package com.kii.extension.sdk.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.query.QueryParam;


public class ApiAccessBuilder {


	private final AppInfo appInfo;

	public ApiAccessBuilder(AppInfo info){
		this.appInfo=info;
	}

	private ApiAccessBuilder(ApiAccessBuilder outer){
		this(new AppInfo(outer.appInfo));
	}

	private String token;
	public ApiAccessBuilder bindToken(String token){
		this.token=token;
		return this;
	}

	private String scopeSubUrl;

	public ApiAccessBuilder bindScope(ScopeType scope,String scopeVal){
		this.scopeSubUrl=scope.getSubUrl(scopeVal);
		return this;
	}


	private String bucketUrl;
	public ApiAccessBuilder  bindBucket(String bucketName){
		this.bucketUrl="/buckets/"+bucketName;
		return this;
	}

	private Map<String,String> optionalHeader=new HashMap<>();

	public ApiAccessBuilder create(Object entity){

		request=new HttpPost(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/objects");

		this.ctxObj=entity;

		return this;
	}

	public ApiAccessBuilder createWithID(Object entity,String id){

		request=new HttpPut(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/objects/"+id);

		this.ctxObj=entity;

		return this;

	}

	public ApiAccessBuilder  getObjectByID(String id){

		request=new HttpGet(appInfo.getSite().getSiteUrl()+scopeSubUrl+bucketUrl+"/objects/"+id);

		return this;
	}

	private HttpUriRequest request;

	public ApiAccessBuilder query(QueryParam query){

		request=new HttpPost(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/query");

		ctxObj=query;

		return this;
	}

	public ApiAccessBuilder delete(String id){
		return this;
	}

	public ApiAccessBuilder updateAll(String id,String content){
		return this;
	}

	public ApiAccessBuilder update(String id,String content){
		return this;
	}

	private Object ctxObj=null;

	private String subUrl;
	public ApiAccessBuilder login(String user,String pwd){
		request=new HttpPost(appInfo.getSite().getSiteUrl()+("/api/oauth2/token"));

		Map<String,String> map=new HashMap<>();
		map.put("username",user);
		map.put("password",pwd);

		ctxObj=map;

		subUrl="/oauth";

		return this;
	}

	public ApiAccessBuilder adminLogin(String user,String pwd){
		request=new HttpPost(appInfo.getSite().getSiteUrl()+("/api/oauth2/token"));

		Map<String,String> map=new HashMap<>();
		map.put("client_id",user);
		map.put("client_secret",pwd);

		ctxObj=map;

		subUrl="/oauth";

		return this;
	}



	public HttpUriRequest generRequest(ObjectMapper mapper){

		request.setHeader("X-Kii-AppID",appInfo.getAppID());
		request.setHeader("X-Kii-AppKey",appInfo.getAppKey());
		if(token!=null){
			request.setHeader("Authorization","Bearer "+token);
		}
		for(Map.Entry<String,String> entry:optionalHeader.entrySet()){
			request.setHeader(entry.getKey(),entry.getValue());
		}

		try {
			if(request instanceof HttpEntityEnclosingRequestBase  &&  ctxObj!=null) {
				String context = mapper.writeValueAsString(ctxObj);
				((HttpEntityEnclosingRequestBase)request).setEntity(new StringEntity(context,ContentType.APPLICATION_JSON));
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return request;
	}



}
