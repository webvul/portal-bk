package com.kii.extension.sdk.impl;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.service.AppBindTool;
import com.kii.extension.sdk.service.TokenBindTool;

@Component
public class KiiCloudRequestInterceptor implements HttpRequestInterceptor {



	@Autowired
	private AppBindTool bindTool;

	@Autowired
	private TokenBindTool tool;

	@Override
	public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {



	}
}
