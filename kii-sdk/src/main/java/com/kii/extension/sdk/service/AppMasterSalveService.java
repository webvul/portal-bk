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




	public boolean isMaster(AppInfo info){


		/*

GET /apps/<masterAppId>/configuration/parameters/isMasterApp
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <appID>
X-Kii-AppKey: <appKey>
		 */

		ApiAccessBuilder builder = getBuilder(info);

		HttpUriRequest request=builder.addSubUrl("/configuration/parameters/isMasterApp").buildCustomCall("GET",null).generRequest(mapper);

		String result=client.executeRequest(request);

		return Boolean.parseBoolean(result);
	}

	public String checkMaster(AppInfo info){
		/*

GET /apps/<slaveAppID>/configuration/parameters/kii.master_app_id
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <slaveAppID>
X-Kii-AppKey: <slaveAppKey>
		 */
		HttpUriRequest  request=getBuilder(info).bindToken(tool.getToken())
				.addSubUrl("/configuration/parameters/kii.master_app_id")
				.buildCustomCall("GET",null).generRequest(mapper);

		return client.executeRequest(request);


	}

	private ApiAccessBuilder getBuilder(AppInfo info) {
		bindToolResolver.setAppInfoDrectly(info);

		return new ApiAccessBuilder(info).bindToken(tool.getToken());
	}


	public void setMaster(AppInfo info){

		/*
		PUT /apps/<masterAppID>/configuration/parameters/isMasterApp
content-type: text/plain
Authorization: Bearer xxxyyyzzz (app-admin / sys-admin)
X-Kii-AppID: <masterAppID>
X-Kii-AppKey: <masterAppKey>
		 */
		ApiAccessBuilder builder = getBuilder(info);

		HttpUriRequest request=builder.addSubUrl("/configuration/parameters/isMasterApp").setContentType("text/plain").buildCustomCall("PUT","true").generRequest(mapper);

		HttpResponse response=client.doRequest(request);

		if(response.getStatusLine().getStatusCode()!=204){
			throw new  KiiCloudException(response);
		}

			/*
		curl -XPOST \
  -H'x-kii-appid: f5795cb7' -H'x-kii-appkey: 12c239d31a3c38dcf53c5208a59d2ddd' \
  -H'authorization: Bearer YM-ke1JasJU9n4G-7zKC5uXPwC6Y_xUWBTaUJdJmeWU' \
  -H'content-type: application/json' \
  https://api-development-beehivecn3.internal.kii.com/api/apps/f5795cb7/oauth2/certs -d {}

		 */

		request=builder.addSubUrl("/oauth2/certs").buildCustomCall("POST","{}").setContentType("application/json").generRequest(mapper);

		client.doRequest(request);

	}




	public void addSalveAppToMaster(AppInfo  masterApp,AppInfo  salveAppInfo){


		ClientInfo info=addSalveApp(masterApp, salveAppInfo);

		registInSalve(info,masterApp,salveAppInfo);

	}



	static String url="http://$(0).$(1).kiiapps.com/api/apps/$(0)/integration/webauth/callback";

	private  ClientInfo addSalveApp(AppInfo  masterApp,AppInfo  salveAppInfo){
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

	private void registInSalve(ClientInfo clientInfo,AppInfo masterApp,AppInfo salveApp){

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

	public void delete(AppInfo masterApp,AppInfo salveApp,ClientInfo clientInfo){

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
