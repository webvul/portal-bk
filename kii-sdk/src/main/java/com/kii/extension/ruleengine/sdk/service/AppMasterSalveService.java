package com.kii.extension.ruleengine.sdk.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.ruleengine.sdk.annotation.AppBindParam;
import com.kii.extension.ruleengine.sdk.context.AdminTokenBindTool;
import com.kii.extension.ruleengine.sdk.entity.AppInfo;
import com.kii.extension.ruleengine.sdk.exception.AppParameterCodeNotFoundException;
import com.kii.extension.ruleengine.sdk.exception.KiiCloudException;
import com.kii.extension.ruleengine.sdk.impl.ApiAccessBuilder;
import com.kii.extension.ruleengine.sdk.impl.KiiCloudClient;

@Component
public class AppMasterSalveService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AdminTokenBindTool tool;


	private ApiAccessBuilder getBuilder(AppInfo info) {

		return new ApiAccessBuilder(info).bindToken(tool.getToken());
	}


	public boolean isMaster(@AppBindParam AppInfo info){

		/*

GET /apps/<masterAppId>/configuration/parameters/isMasterApp
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <appID>
X-Kii-AppKey: <appKey>
		 */

		ApiAccessBuilder builder = getBuilder(info);

		HttpUriRequest request=builder.getSystemParameter("isMasterApp").generRequest(mapper);

		String result=client.executeRequest(request);

		return Boolean.parseBoolean(result);
	}

	public String checkMaster(@AppBindParam  AppInfo info){

		HttpUriRequest  request=getBuilder(info).bindToken(tool.getToken())
				.getSystemParameter("kii.master_app_id")
				.generRequest(mapper);

		try {
			return client.executeRequest(request);
		}catch(AppParameterCodeNotFoundException e){
			return null;
		}

	}



	public void setMaster(@AppBindParam AppInfo info){


		ApiAccessBuilder builder = getBuilder(info);

		HttpUriRequest request=builder.setSystemParameter("isMasterApp","true").generRequest(mapper);

		HttpResponse response=client.doRequest(request);

		if(response.getStatusLine().getStatusCode()!=204){
			throw new  KiiCloudException(response);
		}

		request=builder.addSubUrl("/oauth2/certs").buildCustomCall("POST","{}").setContentType("application/json").generRequest(mapper);

		client.doRequest(request);

	}



	static String url="http://$(0).$(1).kiiapps.com/api/apps/$(0)/integration/webauth/callback";

	public  ClientInfo addSalveApp(@AppBindParam AppInfo  masterApp,AppInfo  salveAppInfo){
		/*
		POST /apps/<masterAppId>/oauth2/clients
content-type: application/vnd.kii.Oauth2ClientCreationRequest+json
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
{
  "externalID": "<slaveAppId>" (optional, only in case client is another Kii app)
  "redirectURI": "https://<slaveAppId>.development-jp.internal.kiiapps.com/api/apps/<slaveAppId>/integration/webauth/callback"" 
}
		 */

		ApiAccessBuilder builder = getBuilder(masterApp);


		String registUrl=StrTemplate.generUrl(url, salveAppInfo.getAppID(),salveAppInfo.getSiteType().getSite());


		Map<String,String> map=new HashMap<>();
		map.put("externalID", salveAppInfo.getAppID());
		map.put("redirectURI", registUrl);

		HttpUriRequest request=builder.addSubUrl("/oauth2/clients")
				.setContentType("application/vnd.kii.Oauth2ClientCreationRequest+json")
				.buildCustomCall("POST", map).generRequest(mapper);

		return client.executeRequestWithCls(request,ClientInfo.class);
	}

	static String api="http://$(0).$(1).kiiapps.com/api/";

	public  void registInSalve(ClientInfo clientInfo,AppInfo masterApp,@AppBindParam AppInfo salveApp){

		/*

curl -v -X POST -H "content-type:application/vnd.kii.AppModificationRequest+json" \
-H 'authorization:Bearer WEJLEaL53tEZy4D4G0mlGH2iXWGdWplAltB2wuPZ0S4' \
-H 'x-kii-appid:<slaveAppId>' -H 'x-kii-appkey:ae79fdbbbd954f513ea662abc1acb0e0' \
http://api-development-jp.internal.kii.com/api/apps/<slaveAppId> -d \
'{
  "socialAuth": {
    "kii.consumer_key": "a9f0gqkte226gbfhbejpbjh9k7m046qdmlug",
    "kii.consumer_secret": "dc9v3j99cjkblkek1gcoa3oueh7bo7g9l3n178l0ohbbkggtt39f2rgba1b4n71n",
    "kii.master_app_id": "<masterAppId>",
    "kii.master_app_site": "https://<masterAppId>.development-jp.internal.kiiapps.com/api/"
  }
}'
		 */

		ApiAccessBuilder builder = getBuilder(salveApp);


		Map<String,String> map=new HashMap<>();

		String masterAppID=masterApp.getAppID();

		String site=masterApp.getSiteType().getSite();

		map.put("kii.consumer_key",clientInfo.clientID);
		map.put("kii.consumer_secret",clientInfo.clientSecret);
		map.put("kii.master_app_id",masterAppID);
		map.put("kii.master_app_site", StrTemplate.generUrl(api, masterAppID,site));

		Map<String,Object> param=new HashMap<>();
		param.put("socialAuth",map);

		HttpUriRequest request=builder.setContentType("application/vnd.kii.AppModificationRequest+json").addSubUrl("/").buildCustomCall("POST",param).generRequest(mapper);

		client.doRequest(request);

	}

	public void delete(@AppBindParam AppInfo masterApp,AppInfo salveApp,ClientInfo clientInfo){

		/*
		DELETE /apps/<masterAppID>/oauth2/clients/<clientID>
content-type: application/vnd.kii.Oauth2ClientCreationRequest+json
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
		 */

		ApiAccessBuilder builder = getBuilder(masterApp);


		HttpUriRequest request=builder.addSubUrl("/oauth2/clients/" + clientInfo.getClientID()).setContentType("application/vnd.kii.Oauth2ClientCreationRequest+json").buildCustomCall("DELETE","").generRequest(mapper);

		client.doRequest(request);

	}

//	public void login(String salveApp,String userName){
//
//		/*
//		DELETE /apps/<masterAppID>/oauth2/clients/<clientID>
//content-type: application/vnd.kii.Oauth2ClientCreationRequest+json
//Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
//		 */
//
//		bindToolResolver.setAppName(masterApp);
//		AppInfo info= bindToolResolver.getAppInfo();
//
//		ApiAccessBuilder builder= new ApiAccessBuilder(info).bindToken(tool.getToken());
//
//		HttpUriRequest request=builder.addSubUrl("/oauth2/clients/" + clientInfo.getClientID()).setContentType("application/vnd.kii.Oauth2ClientCreationRequest+json").buildCustomCall("DELETE","").generRequest(mapper);
//
//		client.doRequest(request);
//
//	}

	public static class ClientInfo{

		private String clientID;

		private String clientSecret;

		public String getClientID() {
			return clientID;
		}

		public void setClientID(String clientID) {
			this.clientID = clientID;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}
	}
}
