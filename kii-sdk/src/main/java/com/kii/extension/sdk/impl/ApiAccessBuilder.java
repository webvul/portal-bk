package com.kii.extension.sdk.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.KiiUser;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.entity.thingif.ActionResult;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.query.QueryParam;


public class ApiAccessBuilder {


	private final AppInfo appInfo;

	public ApiAccessBuilder(AppInfo info) {

		setContentType("application/json");
		this.appInfo = info;
	}

	private ApiAccessBuilder(ApiAccessBuilder outer) {
		this(new AppInfo(outer.appInfo));
	}

	private String token;

	public ApiAccessBuilder bindToken(String token) {
		this.token = token;
		return this;
	}

	private String scopeSubUrl;

	private Map<String, String> optionalHeader = new HashMap<>();

	private Object ctxObj = null;

	private String bucketUrl;

	private HttpUriRequest request;


	public ApiAccessBuilder setContentType(String value) {
		optionalHeader.put("Content-Type", value);
		return this;
	}



	private String subUrl;

	public void setConsumeHeader(String name, String value) {
		optionalHeader.put(name, value);
	}


	public ApiAccessBuilder addSubUrl(String url) {

		this.subUrl = url;

		return this;
	}

	public ApiAccessBuilder buildCustomCall(String type, Object obj) {

		this.ctxObj = obj;
		switch (type.toLowerCase()) {
			case "post":
				request = new HttpPost(appInfo.getAppSubUrl() + subUrl);
				break;
			case "get":
				request = new HttpGet(appInfo.getAppSubUrl() + subUrl);
				break;
			case "put":
				request = new HttpPut(appInfo.getAppSubUrl() + subUrl);
				break;
			case "delete":
				request = new HttpDelete(appInfo.getAppSubUrl() + subUrl);
				break;

		}


		return this;
	}

	public ApiAccessBuilder setSystemParameter(String parameter,String value){

		//"/configuration/parameters/isMasterApp"
		request=new HttpPut(appInfo.getAppSubUrl()+"/configuration/parameters/"+parameter);
		this.ctxObj=value;
		setContentType("text/plain");

		return this;
	}

	public ApiAccessBuilder getSystemParameter(String parameter){

		//"/configuration/parameters/isMasterApp"
		request=new HttpGet(appInfo.getAppSubUrl()+"/configuration/parameters/"+parameter);

		return this;
	}


	public ApiAccessBuilder bindBucketInfo(BucketInfo bucketInfo) {
		return this.bindBucket(bucketInfo.getBucketName()).bindScope(bucketInfo.getScopeType(), bucketInfo.getScopeName());
	}

	public ApiAccessBuilder bindScope(ScopeType scope, String scopeVal) {
		this.scopeSubUrl = scope.getSubUrl(scopeVal);
		return this;
	}


	public ApiAccessBuilder bindBucket(String bucketName) {
		this.bucketUrl = "/buckets/" + bucketName;
		return this;
	}

	public ApiAccessBuilder create(Object entity) {

		request = new HttpPost(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects");

		this.ctxObj = entity;

		return this;
	}

	public ApiAccessBuilder createWithID(Object entity, String id) {

		request = new HttpPut(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects/" + id);

		this.ctxObj = entity;

		return this;

	}

	public ApiAccessBuilder getObjectByID(String id) {

		request = new HttpGet(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects/" + id);

		return this;
	}


	public ApiAccessBuilder query(QueryParam query) {

		request = new HttpPost(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/query");

		this.setContentType("application/vnd.kii.QueryRequest+json");

		ctxObj = query;

		return this;
	}


	public ApiAccessBuilder delete(String id) {

		request = new HttpDelete(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects/" + id);

		return this;
	}

	public ApiAccessBuilder delete(String id, String version) {

		delete(id);

		this.optionalHeader.put("If-Match", version);

		return this;
	}

	public ApiAccessBuilder updateAll(String id, Object entity) {


		request = new HttpPut(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects/" + id);

		this.setContentType("application/vnd." + appInfo.getAppID() + ".mydata+json");

		ctxObj = entity;

		return this;
	}

	public ApiAccessBuilder updateAllWithVersion(String id, Object entity, String version) {

		updateAll(id, entity);

		this.optionalHeader.put("If-Match", version);

		return this;
	}

	public ApiAccessBuilder header(String id) {
		request = new HttpHead(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects/" + id);

//		this.setContentType("application/vnd."+appInfo.getAppID()+".mydata+json");

//		this.optionalHeader.put("X-HTTP-Method-Override", "PATCH");

//		ctxObj=entity;

		return this;
	}

	public ApiAccessBuilder update(String id, Object entity) {
		request = new HttpPost(appInfo.getAppSubUrl() + scopeSubUrl + bucketUrl + "/objects/" + id);

		this.setContentType("application/vnd." + appInfo.getAppID() + ".mydata+json");

		this.optionalHeader.put("X-HTTP-Method-Override", "PATCH");

		ctxObj = entity;

		return this;
	}

	public ApiAccessBuilder updateWithVersion(String id, Object entity, String version) {
		update(id, entity);

		this.optionalHeader.put("If-Match", version);

		return this;
	}

	//=================
	//user relation
	//=================

	public ApiAccessBuilder setUserStatus(String userID, boolean status) {

		request = new HttpPut(appInfo.getAppSubUrl() + "/users/" + userID + "/status");

		this.setContentType("application/vnd.kii.UserStatusUpdateRequest+json");


		Map<String, Object> map = new HashMap<>();

		map.put("disabled", status);

		this.ctxObj = map;

		return this;
	}


	public ApiAccessBuilder createUser(KiiUser user) {


		request = new HttpPost(appInfo.getAppSubUrl() + "/users");

		this.setContentType("application/vnd.kii.RegistrationRequest+json");
		ctxObj = user;

		return this;
	}

	public ApiAccessBuilder deleteUser(String userInfo, String type) {

		if (type == null) {
			request = new HttpDelete(appInfo.getAppSubUrl() + "/users/" + userInfo);

		} else {
			request = new HttpDelete(appInfo.getAppSubUrl() + "/users/" + type + ":" + userInfo);
		}
		return this;
	}

	public ApiAccessBuilder loginWithCode(String code, String clientID) {

		request = new HttpPost(appInfo.getSiteUrl() + ("/api/oauth2/token"));

		List<NameValuePair> postParameters = new ArrayList<>();
		postParameters.add(new BasicNameValuePair("grant_type", "code"));
		postParameters.add(new BasicNameValuePair("code", code));
		postParameters.add(new BasicNameValuePair("client_id", clientID));
//		postParameters.add(new BasicNameValuePair("grant_type","code"));

		/*
		Map<String,String> map=new HashMap<>();
		map.put("grant_type","code");
		map.put("code", code);
		map.put("client_id",clientID);
		map.put("redirect_uri","foo");
		*/
		try {
			((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ctxObj = null;
		return this;
	}


	public ApiAccessBuilder login(String user, String pwd) {
		request = new HttpPost(appInfo.getSiteUrl() + ("/api/oauth2/token"));

		Map<String, String> map = new HashMap<>();
		map.put("username", user);
		map.put("password", pwd);

		ctxObj = map;

		return this;
	}

	public ApiAccessBuilder adminLogin(String user, String pwd) {
		request = new HttpPost(appInfo.getSiteUrl() + ("/api/oauth2/token"));

		Map<String, String> map = new HashMap<>();
		map.put("client_id", user);
		map.put("client_secret", pwd);

		ctxObj = map;

		return this;
	}


	/**
	 * ==================
	 * thing if
	 * ==================
	 */

	public ApiAccessBuilder thingOnboarding(OnBoardingParam onboardParam){

		/*
		> POST /apps/<appid>/onboardings
"Content-Type:application/vnd.kii.onboardingWithThingIDByOwner+json"

{

		 */
		request=new HttpPost(appInfo.getThingIfSubUrl()+"/onboardings");

		this.ctxObj=onboardParam;

		if(!StringUtils.isEmpty(onboardParam.getThingID())){

			this.setContentType("application/vnd.kii.onboardingWithThingIDByOwner+json");

		}else if(!StringUtils.isEmpty(onboardParam.getVendorThingID())){
			this.setContentType("application/vnd.kii.onboardingWithVendorThingIDByOwner+json");

		}else{
			throw new KiiCloudException();
		}

		return this;
	}


	public ApiAccessBuilder sendCommand(String thingID, ThingCommand command){

		request=new HttpPost(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/commands");

		this.ctxObj=command;

		return this;
	}

	public ApiAccessBuilder getCommand(String thingID,String commandID){
		request=new HttpGet(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/commands/"+commandID);

		return this;
	}

	public ApiAccessBuilder queryCommands(String thingID, int bestLimit,String pageKey){
//> GET /thing-if/apps/{appID}/targets/{targetType:targetID}/commands?paginationKey=XXXXX?bestEffortLimit=XXXXX

		String subUrl=appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/commands?";
		subUrl+="bestEffortLimit="+bestLimit+"&";
		if(!StringUtils.isEmpty(pageKey)){
			subUrl+="paginationKey="+pageKey;
		}
		request=new HttpPost(subUrl);

		return this;
	}

	public ApiAccessBuilder submitCommand(String thingID,String commandID, List<Map<String,ActionResult>>  results){
//		> GET targetID}/commands/{commandID}/action-results

		request=new HttpPut(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/commands/"+commandID+"/action-results");

		ctxObj=results;
		return this;
	}

	public ApiAccessBuilder getThingStatus(String thingID){
//		> GET /thing-if/apps/{appID}/targets/{targetType:targetID}/states/

		request=new HttpGet(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/states");

		return this;
	}

	public ApiAccessBuilder  setThingStatus(String thingID,ThingStatus status){

		request=new HttpPut(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/states");

		ctxObj=status;
		return this;

	}

	//================================
	//business relation
	//================================

	public ApiAccessBuilder createTrigger(String thingID,ThingTrigger trigger){

		request=new HttpPost(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/triggers");

		ctxObj=trigger;

		return this;


	}

	public ApiAccessBuilder updateTrigger(String thingID,String triggerID,ThingTrigger trigger){

		request=new HttpPatch(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/triggers/"+triggerID);

		ctxObj=trigger;

		return this;


	}

	public ApiAccessBuilder deleteTrigger(String thingID,String triggerID){

		request=new HttpDelete(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/triggers/"+triggerID);

		return this;

	}

	public ApiAccessBuilder getTrigger(String thingID,String triggerID){


		request=new HttpGet(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/triggers/"+triggerID);

		return this;
	}


	//=============================
	//server extension
	//=============================

	public ApiAccessBuilder deployServiceCode(String codeCtx){

		request =new HttpPost(appInfo.getAppSubUrl()+"/server-code");

//		this.setConsumeHeader("Accept","application/vnd.kii.ServerCodeDeploymentResponse+json");
		this.setContentType("application/javascript");
		this.ctxObj=codeCtx;

		return this;
	}

	public ApiAccessBuilder deployHook(String hookCtx,String version){
		request =new HttpPut(appInfo.getAppSubUrl()+"/hooks/versions/"+version);

		this.setContentType("application/vnd.kii.HooksDeploymentRequest+json");
		this.ctxObj=hookCtx;
		return this;
	}

	public ApiAccessBuilder setCurrentVersion(String version){
		request =new HttpPut(appInfo.getAppSubUrl()+"/server-code/versions/current");

		this.setContentType("text/plain");
		this.ctxObj=version;
		return this;
	}

	public ApiAccessBuilder callServiceExtension(String serviceName,Object param){
		///apps/{appID}/server-code/versions/current/{ENDPOINT_NAME}
		request =new HttpPost(appInfo.getAppSubUrl()+"/server-code/versions/current/"+serviceName);

		this.ctxObj=param;
		return this;
	}

	public ApiAccessBuilder getCurrentVersion(){
		request =new HttpGet(appInfo.getAppSubUrl()+"/server-code/versions/current");

		return this;
	}

	public ApiAccessBuilder getServiceCode(String version){

		request =new HttpGet(appInfo.getAppSubUrl()+"/server-code/versions/"+version);

		return this;
	}

	public ApiAccessBuilder getHookConfig(String version){

		request =new HttpGet(appInfo.getAppSubUrl()+"/hooks/versions/"+version);

		return this;
	}

	//==============================
	//
	//==============================

	public HttpUriRequest generRequest(ObjectMapper mapper) {

		request.setHeader("X-Kii-AppID", appInfo.getAppID());
		request.setHeader("X-Kii-AppKey", appInfo.getAppKey());


		if (token != null) {
			request.setHeader("Authorization", "Bearer " + token);
		}
		for (Map.Entry<String, String> entry : optionalHeader.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}

		if (request instanceof HttpEntityEnclosingRequestBase && ctxObj != null) {

			if (ctxObj instanceof String) {
				((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity((String) ctxObj, Charsets.UTF_8));
			} else {
				try {
					String context = mapper.writeValueAsString(ctxObj);
					((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(context, Charsets.UTF_8));

				} catch (JsonProcessingException e) {
					throw new IllegalArgumentException(e);
				}

			}
		}


		ctxObj = null;
		optionalHeader.clear();

		return request;
	}


}
