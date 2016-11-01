package com.kii.extension.sdk.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

public class PortalApiAccessBuilder {


	private final String baseUrl;

	private HttpUriRequest request;


	public PortalApiAccessBuilder(String baseUrl){

		this.baseUrl=baseUrl;

	}

	String subUrl=null;

	Object ctxObj=null;

	public PortalApiAccessBuilder buildLoginPrepare(){

		request=new HttpGet(baseUrl+"/login");

		return this;

	}

	public PortalApiAccessBuilder buildLogin(String userName,String pwd,String token)  {

		String fullUrl=baseUrl+"/sessions?email="+userName+"&password="+pwd+"&commit=login&authenticity_token="+token;

		try {
		List<NameValuePair> list=new ArrayList<>();
		list.add(new BasicNameValuePair("email",userName));
		list.add(new BasicNameValuePair("password",pwd));
		list.add(new BasicNameValuePair("commit","login"));
		list.add(new BasicNameValuePair("authenticity_token",token));


			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
			request = new HttpPost(fullUrl);
			((HttpPost)request).setEntity(entity);
		}catch(Exception  e){
			throw new IllegalArgumentException(e);
		}
		request.addHeader("Referer",baseUrl+"/login");

		return this;
	}



	public PortalApiAccessBuilder buildAppList(){

		request=new HttpGet(baseUrl+"/v2api/apps");
		request.addHeader("X-Requested-With","XMLHttpRequest");
		request.setHeader("Accept","application/json");

		return this;
	}

	public PortalApiAccessBuilder buildAppDetail(String appID){

		request=new HttpGet(baseUrl+"/v2api/apps/"+appID);

		return this;
	}

	public PortalApiAccessBuilder buildAppSecret(String appID){

		request=new HttpGet(baseUrl+"/v2api/apps/"+appID+"/secret");

		return this;
	}

	public HttpUriRequest generRequest(){

		return request;
	}

}
