package com.kii.extension.sdk.impl;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
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


	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls) {

		return executeRequestWithCls(request,cls,null);

	}

	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls,HttpContext context){


		String result=executeRequest(request,context);

		try {
			return  mapper.readValue(result, cls);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}



	public HttpResponse doRequest(HttpUriRequest request,HttpContext context){
		HttpResponse response=httpTool.doRequest(request,context);

		factory.checkResponse(response, request.getURI());
		return response;

	}

	public HttpResponse doRequest(HttpUriRequest request) {


		return doRequest(request, null);

	}


	public String executeRequest(HttpUriRequest request) {
		return executeRequest(request, null);
	}


	private String executeRequest(HttpUriRequest request,HttpContext context){


			HttpResponse response=doRequest(request, context);


			if(request.getMethod().equals("DELETE")){
				return "";
			}

			return HttpUtils.getResponseBody(response);
	}


	public Future<HttpResponse> asyncExecuteRequest(HttpUriRequest request, FutureCallback<HttpResponse>  callback){

		return  httpTool.asyncExecuteRequest(request,callback);

	}





}
