package com.kii.extension.sdk.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.sdk.context.AdminTokenBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class AppMasterSalveService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;

	@Autowired
	private AdminTokenBindTool tool;


//	private ApiAccessBuilder getBuilder(){
//
//	}


	public void linkMasterSalve(String masterName,String[] salveArray){


		this.setMaster(masterName);

		for(String salve:salveArray){

			ClientInfo info=this.addSalveApp(masterName, salve);
			this.registInSalve(info,masterName,salve);
		}


	}

	public boolean isMaster(String appName){


		/*

GET /apps/<masterAppId>/configuration/parameters/isMasterApp
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <appID>
X-Kii-AppKey: <appKey>
		 */

		ApiAccessBuilder builder = getBuilder(appName);

		HttpUriRequest request=builder.addSubUrl("/configuration/parameters/isMasterApp").buildCustomCall("GET",null).generRequest(mapper);

		String result=client.executeRequest(request);

		return Boolean.parseBoolean(result);
	}

	public String checkMaster(String salveApp){
		/*

GET /apps/<slaveAppID>/configuration/parameters/kii.master_app_id
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <slaveAppID>
X-Kii-AppKey: <slaveAppKey>
		 */
		HttpUriRequest  request=getBuilder(salveApp).bindToken(tool.getToken())
				.addSubUrl("/configuration/parameters/kii.master_app_id")
				.buildCustomCall("GET",null).generRequest(mapper);

		return client.executeRequest(request);


	}

	private ApiAccessBuilder getBuilder(String appName) {
		bindToolResolver.setAppName(appName,false);
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info).bindToken(tool.getToken());
	}


	public void setMaster(String appName){

		/*
		PUT /apps/<masterAppID>/configuration/parameters/isMasterApp
content-type: text/plain
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <masterAppID>
X-Kii-AppKey: <masterAppKey>
		 */
		ApiAccessBuilder builder = getBuilder(appName);

		HttpUriRequest request=builder.addSubUrl("/configuration/parameters/isMasterApp").setContentType("text/plain").buildCustomCall("PUT","true").generRequest(mapper);

		HttpResponse response=client.doRequest(request);

		if(response.getStatusLine().getStatusCode()!=204){
			throw new  KiiCloudException(response);
		}

	}

	static String url="https://$(0).$(1).kii.com/api/apps/$(0)/integration/webauth/callback";

	public ClientInfo addSalveApp(String masterApp,String salveApp){
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

		AppInfo salveAppInfo=bindToolResolver.getAppInfoByName(salveApp);

		String registUrl=StrTemplate.generUrl(url, salveAppInfo.getAppID(),salveAppInfo.getSiteType().getSite());


		Map<String,String> map=new HashMap<>();
		map.put("externalID", salveApp);
		map.put("redirectURI", registUrl);

		HttpUriRequest request=builder.addSubUrl("/oauth2/clients")
				.setContentType("application/vnd.kii.Oauth2ClientCreationRequest+json")
				.buildCustomCall("POST", map).generRequest(mapper);

		return client.executeRequestWithCls(request,ClientInfo.class);
	}

	static String api="https://$(0).$(1).kii.com/api/";

	public void registInSalve(ClientInfo clientInfo,String masterApp,String salveApp){

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

		String masterAppID=bindToolResolver.getAppInfoByName(masterApp).getAppID();

		String site=bindToolResolver.getAppInfoByName(masterApp).getSiteType().getSite();

		map.put("kii.consumer_key",clientInfo.clientID);
		map.put("kii.consumer_secret",clientInfo.clientSecret);
		map.put("kii.master_app_id",masterAppID);
		map.put("kii.master_app_site", StrTemplate.generUrl(api, masterAppID,site));

		Map<String,Object> param=new HashMap<>();
		param.put("socialAuth",map);

		HttpUriRequest request=builder.setContentType("application/vnd.kii.AppModificationRequest+json").addSubUrl("/").buildCustomCall("POST",param).generRequest(mapper);

		client.doRequest(request);

	}

	public void delete(String masterApp,String salveApp,ClientInfo clientInfo){

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
