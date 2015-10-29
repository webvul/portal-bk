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


@Component
public class KiiCloudClient {


	HttpClient httpClient=HttpClients.createDefault();


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

			return response;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public HttpResponse doRequest(HttpUriRequest request){

		return doRequest(request,null);

	}


	public String executeRequest(HttpUriRequest request){
		return executeRequest(request,null);
	}


	public String executeRequest(HttpUriRequest request,HttpClientContext context){

		try{
			HttpResponse response=doRequest(request,context);


			if(request.getMethod().equals("DELETE")){
				return "";
			}

			HttpEntity entity=response.getEntity();
			if(entity!=null) {

				String result = new String(FileCopyUtils.copyToByteArray(response.getEntity().getContent()), "UTF-8");

				return result;
			}else{
				return "";
			}

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}



}
