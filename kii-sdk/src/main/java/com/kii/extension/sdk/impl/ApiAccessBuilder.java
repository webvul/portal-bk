package com.kii.extension.sdk.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpDelete;
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
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.query.QueryParam;


public class ApiAccessBuilder {


	private final AppInfo appInfo;

	public ApiAccessBuilder(AppInfo info){

		setContentType("application/json");
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

	private Map<String,String> optionalHeader=new HashMap<>();

	private Object ctxObj=null;

	private String bucketUrl;

	private HttpUriRequest request;



	private void setContentType(String value){
		optionalHeader.put("Content-Type",value);
	}


	public ApiAccessBuilder bindBucketInfo(BucketInfo bucketInfo){
		return this.bindBucket(bucketInfo.getBucketName()).bindScope(bucketInfo.getScopeType(),bucketInfo.getScopeName());
	}

	public ApiAccessBuilder bindScope(ScopeType scope,String scopeVal){
		this.scopeSubUrl=scope.getSubUrl(scopeVal);
		return this;
	}


	public ApiAccessBuilder  bindBucket(String bucketName){
		this.bucketUrl="/buckets/"+bucketName;
		return this;
	}

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

		request=new HttpGet(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/objects/"+id);

		return this;
	}


	public ApiAccessBuilder query(QueryParam query){

		request=new HttpPost(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/query");

		this.setContentType("application/vnd.kii.QueryRequest+json");

		ctxObj=query;

		return this;
	}



	public ApiAccessBuilder delete(String id){

		request=new HttpDelete(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/objects/"+id);

		return this;
	}

	public ApiAccessBuilder delete(String id,String version){

		delete(id);

		this.optionalHeader.put("If-Match",version);

		return this;
	}

	public ApiAccessBuilder updateAll(String id,Object entity){


		request=new HttpPut(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/objects/"+id);

		this.setContentType("application/vnd."+appInfo.getAppID()+".mydata+json");

		ctxObj=entity;

		return this;
	}

	public ApiAccessBuilder updateAllWithVersion(String id,Object entity,String version){

		updateAll(id, entity);

		this.optionalHeader.put("If-Match",version);

		return this;
	}

	public ApiAccessBuilder update(String id,Object entity){
		request=new HttpPost(appInfo.getAppSubUrl()+scopeSubUrl+bucketUrl+"/objects/"+id);

		this.setContentType("application/vnd."+appInfo.getAppID()+".mydata+json");

		this.optionalHeader.put("X-HTTP-Method-Override", "PATCH");

		ctxObj=entity;

		return this;
	}

	public ApiAccessBuilder updateWithVersion(String id,Object entity,String version){
		update(id,entity);

		this.optionalHeader.put("If-Match",version);

		return this;
	}



	public ApiAccessBuilder login(String user,String pwd){
		request=new HttpPost(appInfo.getSite().getSiteUrl()+("/api/oauth2/token"));

		Map<String,String> map=new HashMap<>();
		map.put("username",user);
		map.put("password",pwd);

		ctxObj=map;

		return this;
	}

	public ApiAccessBuilder adminLogin(String user,String pwd){
		request=new HttpPost(appInfo.getSite().getSiteUrl()+("/api/oauth2/token"));

		Map<String,String> map=new HashMap<>();
		map.put("client_id",user);
		map.put("client_secret",pwd);

		ctxObj=map;

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
