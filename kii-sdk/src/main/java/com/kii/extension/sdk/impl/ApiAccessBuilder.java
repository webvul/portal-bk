package com.kii.extension.sdk.impl;

import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
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
import com.kii.extension.sdk.entity.trigger.ThingTrigger;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.query.ThingQueryParam;


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

	private void setBasicToken(){

		String str=appInfo.getAppID()+":"+appInfo.getAppKey();
		String basicToken= "Basic "+ Base64.getEncoder().encodeToString(str.getBytes());

		setConsumeHeader("Authorization",basicToken);
	}

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

	public ApiAccessBuilder bindAppScope() {
		this.scopeSubUrl = "";
		return this;
	}

	public ApiAccessBuilder queryBuckets(){

		request = new HttpGet(appInfo.getAppSubUrl() + scopeSubUrl+ "/buckets");


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

	public ApiAccessBuilder getUserDetail() {


		request = new HttpGet(appInfo.getAppSubUrl() + "/users/me");

//		this.setContentType("application/vnd.kii.RegistrationRequest+json");
//		ctxObj = user;

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

//	public ApiAccessBuilder loginWithCode(String code, String clientID) {
//
//		request = new HttpPost(appInfo.getSiteUrl() + ("/api/oauth2/token"));
//
//		List<NameValuePair> postParameters = new ArrayList<>();
//		postParameters.add(new BasicNameValuePair("grant_type", "code"));
//		postParameters.add(new BasicNameValuePair("code", code));
//		postParameters.add(new BasicNameValuePair("client_id", clientID));
//
//		try {
//			((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(postParameters));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		ctxObj = null;
//		return this;
//	}


	public ApiAccessBuilder login(String user, String pwd) {
		request = new HttpPost(appInfo.getAppSubUrl() + ("/oauth2/token"));

		setBasicToken();

//		setContentType("application/x-www-form-urlencoded");

		Map<String, String> map = new HashMap<>();
		map.put("grant_type","password");
		map.put("username", user);
		map.put("password", pwd);

		ctxObj = map;

		return this;
	}

	public ApiAccessBuilder adminLogin(String user, String pwd) {
		request = new HttpPost(appInfo.getAppSubUrl() + ("/oauth2/token"));

		setBasicToken();

		Map<String, String> map = new HashMap<>();
		map.put("grant_type","client_credentials");
		map.put("client_id", user);
		map.put("client_secret", pwd);
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.HOUR,1);

		map.put("expiresAt",String.valueOf(cal.getTime().getTime()));

		ctxObj = map;

		return this;
	}

	public ApiAccessBuilder changePassword(String oldPassword,String newPassword){

		request=new HttpPut(appInfo.getAppSubUrl()+"/users/me/password");

		this.setContentType("application/vnd.kii.ChangePasswordRequest+json");


		Map<String,Object> map=new HashMap<>();

		map.put("oldPassword",oldPassword);
		map.put("newPassword",newPassword);

		this.ctxObj=map;

		return this;
	}

	//================================
	//thing relation
	//================================

	/**
	 * generate request for delete thing by Kii thing id
	 * @param thingID Kii thing id
	 * @return
     */
	public ApiAccessBuilder deleteThing(String thingID) {
		request=new HttpDelete(appInfo.getAppSubUrl()+"/things/"+thingID);

		return this;
	}

	public ApiAccessBuilder thingOnboarding(OnBoardingParam onboardParam){

		/*
		> POST /apps/<appid>/onboardings
"Content-Type:application/vnd.kii.onboardingWithThingIDByOwner+json"

{

		 */
		request=new HttpPost(appInfo.getThingIfSubUrl()+"/onboardings");

		this.ctxObj=onboardParam;

		setBasicToken();

		if(!StringUtils.isEmpty(onboardParam.getThingID())){

			this.setContentType("application/vnd.kii.OnboardingWithThingIDByThing+json");

		}else if(!StringUtils.isEmpty(onboardParam.getVendorThingID())){
			this.setContentType("application/vnd.kii.OnboardingWithVendorThingIDByThing+json");

		}else{
			throw new KiiCloudException();
		}

		return this;
	}

	public ApiAccessBuilder thingOnboardingByOwner(OnBoardingParam onboardParam){

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

	public ApiAccessBuilder queryCommands(String thingID, QueryParam query){
//https://api-development-beehivecn3.internal.kii.com/api/apps/493e83c9/things/th.f83120e36100-09e9-6e11-8aee-0bc5a488/buckets/_commands/query
		String subUrl=appInfo.getAppSubUrl()+"/things/"+thingID+"/buckets/_commands/query";

		request=new HttpPost(subUrl);

		this.setContentType("application/vnd.kii.QueryRequest+json");

		ctxObj = query;

		return this;
	}
	public ApiAccessBuilder deleteCommand(String thingID, String commandId){
//apps/493e83c9/things/th.f83120e36100-09e9-6e11-8aee-0bc5a488/buckets/_commands/objects/4e793c60-f73e-11e6-9e90-00163e02138f"
		String subUrl=appInfo.getAppSubUrl()+"/things/"+thingID+"/buckets/_commands/objects/"+commandId;

		request=new HttpDelete(subUrl);

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
	public ApiAccessBuilder getThingGateway(String thingID){
//		> GET /thing-if/apps/{appID}/things/thingID/gateway

		request=new HttpGet(appInfo.getAppSubUrl()+"/things/"+thingID+"/gateway");

		return this;
	}

	public ApiAccessBuilder  setThingStatus(String thingID,ThingStatus status){

		request=new HttpPut(appInfo.getThingIfSubUrl()+"/targets/THING:"+thingID+"/states");

		ctxObj=status;
		return this;

	}

	//===============================
	//installation relation
	//===============================
	/*

curl -v -X POST -H "content-type:application/vnd.kii.InstallationCreationRequest+json"
-H "Authorization:bearer KzGQrYhWgxqsYFLhwNWoj6456PaVQ3YWxG6Aq0G-AA"
-H 'x-kii-appid:sandbox' -H 'x-kii-appkey:dummy'
 "https://api-development-jp.kii.com/api/apps/sandbox/installations" -d '{"installationType":"MQTT", "development":false}'
	 */
	public ApiAccessBuilder  getThingInstallation(){

		request=new HttpPost(appInfo.getAppSubUrl()+"/installations");

		setContentType("application/vnd.kii.InstallationCreationRequest+json");

		Map<String,Object> params=new HashMap<>();

		params.put("installationType","MQTT");
		params.put("development",false);

		ctxObj=params;

		return this;

	}

	public ApiAccessBuilder  getThingInstallationByAdmin(String thingID){

		request=new HttpPost(appInfo.getAppSubUrl()+"/installations");

		setContentType("application/vnd.kii.InstallationCreationRequest+json");

		Map<String,Object> params=new HashMap<>();

		params.put("installationType","MQTT");
		params.put("development",false);
		params.put("thingID",thingID);

		ctxObj=params;

		return this;

	}

	public ApiAccessBuilder getInstallationByID(String installationID){
//GET /apps/<appID>/installations/<installationID>
		request=new HttpGet(appInfo.getAppSubUrl()+"/installations/"+installationID);

		return this;
	}

	public ApiAccessBuilder getInstallationsByThingID(String thingID){
		// GET /apps/<appID>/installations?thingID=<thingID>
		request=new HttpGet(appInfo.getAppSubUrl()+"/installations?thingID="+thingID);

		return this;
	}

	public ApiAccessBuilder getMQTTEndPointByInstallationID(String installationID){


		//GET /api/apps/{appid}/installations/{installationID}/mqtt-endpoint

		request=new HttpGet(appInfo.getAppSubUrl()+"/installations/"+installationID+"/mqtt-endpoint");

		return this;
	}

	//================================
	//gateway relation
	//================================

	public ApiAccessBuilder getEndNodesOfGateway(String thingID, int bestEffortLimit, String paginationKey) {
//		/apps/<appID>/things/<thingID>/end-nodes?bestEffortLimit={limit}&paginationKey={paginationKey}

		String subUrl=appInfo.getThingIfSubUrl()+"/things/"+thingID+"/end-nodes?";
		subUrl+="bestEffortLimit="+bestEffortLimit+"&";
		if(!StringUtils.isEmpty(paginationKey)){
			subUrl+="paginationKey="+paginationKey;
		}
		request = new HttpGet(subUrl);

		this.setContentType("application/vnd.kii.ThingEndNodesRetrievalResponse+json");

		return this;
	}

	//================================
	//business trigger relation
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
	//================================
	//query thing
	//================================

	public ApiAccessBuilder getThings(ThingQueryParam query) {

		String subUrl=appInfo.getAppSubUrl()+"/things/query";
		request = new HttpPost(subUrl);
		this.ctxObj = query;
		this.setContentType("application/vnd.kii.thingqueryrequest+json");

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
