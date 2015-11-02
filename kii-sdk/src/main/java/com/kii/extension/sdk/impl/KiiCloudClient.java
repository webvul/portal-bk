package com.kii.extension.sdk.impl;

import javax.annotation.PostConstruct;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.exception.ExceptionFactory;


@Component
public class KiiCloudClient {


	HttpClient httpClient=HttpClients.createDefault();

	@Autowired
	private ExceptionFactory factory;

	@Autowired
	private ObjectMapper mapper;

	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls) {

		return executeRequestWithCls(request,cls,null);

	}

	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls,HttpClientContext context){


		String result=executeRequest(request,context);

		try {
			return  mapper.readValue(result, cls);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}

	public HttpResponse doRequest(HttpUriRequest request,HttpClientContext context){
		try{
			HttpResponse response=null;
			if(context==null){
				response = httpClient.execute(request);

			}else {
				response = httpClient.execute(request, context);
			}


			factory.checkResponse(response);

			return response;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public HttpResponse doRequest(HttpUriRequest request){

		return doRequest(request, null);

	}


	public String executeRequest(HttpUriRequest request){
		return executeRequest(request, null);
	}


	private String executeRequest(HttpUriRequest request,HttpClientContext context){


			HttpResponse response=doRequest(request, context);


			if(request.getMethod().equals("DELETE")){
				return "";
			}

			return HttpUtils.getResponseBody(response);



	}





}
