package com.kii.extension.sdk.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.SiteType;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class FederatedAuthService {

	private Logger log= LoggerFactory.getLogger(FederatedAuthService.class);


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	private String authInitUrl="http://$(0).$(1).kiiapps.com/api/apps/$(0)/integration/webauth/connect?id=kii";




	public String getAuthUrl(AppInfo salveInfo){

		/*
GET https://<slaveAppId>.<kiiapps-domain>/api/apps/<slaveAppId>/integration/webauth/connect?id=kii
		 */

		String fullUrl= StrTemplate.generUrl(authInitUrl, salveInfo.getAppID(), salveInfo.getSiteType().getSite());

		HttpUriRequest request=new HttpGet(fullUrl);


		HttpContext context=new BasicHttpContext();

		HttpResponse response=client.doRequest(request,context);

		HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
				HttpCoreContext.HTTP_REQUEST);
		HttpHost currentHost = (HttpHost)  context.getAttribute(
				HttpCoreContext.HTTP_TARGET_HOST);

		String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());

		log.info(currentUrl);

		/*
		http://b8ca23d0.development-beehivecn3.internal.kiiapps.com/app/oauth2-frontend/login/b8ca23d0?
		response_type=code&client_id=a9f0o1o962bnvs1qvaq2uu6b09pu8ntkjrrp
		&redirect_uri=http://c1744915.development-beehivecn3.internal.kiiapps.com/api/apps/c1744915/integration/webauth/callback&state
		&scope=openid&master_app_key=3f845782361aa01014b4a7fe418e7f25
		 */

		return currentUrl;

	}

	private static String authUrl="http://$(0).$(1).kiiapps.com/api/apps/$(0)/oauth2/login";

	public  String  generAuthRequest(String fullUrl,SiteType site,String user,String pwd)  {

		int idx=fullUrl.indexOf("?");

		String url=fullUrl.substring(0, idx);

		String appID=url.substring(url.lastIndexOf("/")+1);

		String fullMasterAuthUrl=StrTemplate.generUrl(authUrl, appID, site.getSite());

		HttpPost post=new HttpPost(fullMasterAuthUrl);

		List<NameValuePair> postParameters=new ArrayList<>();

		String[] arrays=fullUrl.substring(idx+1).split("&");
		for(String seg:arrays){

			int i=seg.indexOf("=");
			if(i==-1){
				postParameters.add(new BasicNameValuePair(seg, ""));
			}else {
				String key=seg.substring(0, i);
				if(key.equals("master_app_key")){
					continue;
				}
				String value=seg.substring(i + 1);
				postParameters.add(new BasicNameValuePair(key,value));
			}
		}

		postParameters.add(new BasicNameValuePair("username",user));
		postParameters.add(new BasicNameValuePair("password",pwd));

		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}

		HttpContext context=new BasicHttpContext();

		HttpResponse response=client.doRequest(post,context);


		HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
				HttpCoreContext.HTTP_REQUEST);
		HttpHost currentHost = (HttpHost)  context.getAttribute(
				HttpCoreContext.HTTP_TARGET_HOST);

		String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
//
//		String  redirectUrl=response.getFirstHeader("Location").getValue();
//
//		int codeIdx=redirectUrl.indexOf("code=");
//		int codeEnd=redirectUrl.indexOf("&",codeIdx+1);
//
//		String code=redirectUrl.substring(codeIdx+5,codeEnd);
//
//		log.info(currentUrl);


		return  null;
	}


}
