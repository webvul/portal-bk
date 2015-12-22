package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class ServiceExtensionService {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	@Autowired
	private TokenBindToolResolver tool;

	private ApiAccessBuilder getBuilder(){
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info).bindToken(tool.getToken());
	}



	public <T> T callServiceExtension(String serviceName,Object param,  Class<T> cls){

		HttpUriRequest request=getBuilder().callServiceExtension(serviceName,param).generRequest(mapper);

		return  client.executeRequestWithCls(request,cls);

	}

	public String deployServiceExtension(String serviceCtx){

		HttpUriRequest request=getBuilder().deployServiceCode(serviceCtx).generRequest(mapper);

		Map<String,Object> result=client.executeRequestWithCls(request,Map.class);

		return (String) result.get("versionID");
	}


	public void deployServiceExtension(String serviceCtx,String hookDescription){

		final AppInfo appInfo=bindToolResolver.getAppInfo();

		HttpUriRequest request=getBuilder().deployServiceCode(serviceCtx).generRequest(mapper);

		client.syncExecuteRequest(request, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse httpResponse) {

				bindToolResolver.setAppInfoDirectly(appInfo);

				String response= HttpUtils.getResponseBody(httpResponse);

				Map<String,Object> result= null;
				try {
					result = mapper.readValue(response,Map.class);
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
				String version= (String) result.get("versionID");

				HttpUriRequest hookRequest=getBuilder().deployHook(hookDescription,version).generRequest(mapper);

				client.syncExecuteRequest(hookRequest, new FutureCallback<HttpResponse>() {
					@Override
					public void completed(HttpResponse httpResponse) {
						bindToolResolver.setAppInfoDirectly(appInfo);

						HttpUriRequest setVerRequest=getBuilder().setCurrentVersion(version).generRequest(mapper);

						client.executeRequest(setVerRequest);
					}

					@Override
					public void failed(Exception e) {

					}

					@Override
					public void cancelled() {

					}
				});
			}

			@Override
			public void failed(Exception e) {

			}

			@Override
			public void cancelled() {

			}
		});

	}
}
