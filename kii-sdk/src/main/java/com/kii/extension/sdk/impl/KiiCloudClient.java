package com.kii.extension.sdk.impl;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpTool;
import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.exception.ExceptionFactory;


@Component
public class KiiCloudClient {

	private Logger log= LoggerFactory.getLogger(KiiCloudClient.class);

	@Autowired
	private ExceptionFactory factory;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private HttpTool httpTool;


	public HttpContext getContext(){
		return httpTool.getContext();

	}


	public CookieStore getCookieStore(){
		return httpTool.getCookieStore();
	}

	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls){
		return executeRequestWithCls(request,cls,false);
	}


	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls,boolean withCookie){


		String result=executeRequest(request,withCookie);

		try {
			return  mapper.readValue(result, cls);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}
	public HttpResponse doRequest(HttpUriRequest request) {
		return doRequest(request,false);
	}

	public HttpResponse doRequest(HttpUriRequest request,boolean withCookie) {


		HttpResponse response= httpTool.doRequest(request,withCookie);


		factory.checkResponse(response, request.getURI());


		return response;

	}

	public  String executeRequest(HttpUriRequest request){
		return executeRequest(request,false);
	}


	public  String executeRequest(HttpUriRequest request,boolean withCookie){


		HttpResponse response=doRequest(request,withCookie);


		if(request.getMethod().equals("DELETE")){
			return "";
		}

		return HttpUtils.getResponseBody(response);
	}
	public Future<HttpResponse> asyncExecuteRequest(HttpUriRequest request,FutureCallback<HttpResponse>  callback){

		return asyncExecuteRequest(request,false,callback);
	}



	public Future<HttpResponse> asyncExecuteRequest(HttpUriRequest request, boolean withCookie,FutureCallback<HttpResponse>  callback){

		return  httpTool.asyncExecuteRequest(request,callback);

	}








}
