package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.exception.AppParameterCodeNotFoundException;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class ServiceExtensionService {

	private Logger log= LoggerFactory.getLogger(ServiceExtensionService.class);

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

		return new ApiAccessBuilder(info).bindToken(bindToolResolver.getToken());
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

	public String getServiceExtension(String version){

		HttpUriRequest request=getBuilder().getServiceCode(version).generRequest(mapper);

		return client.executeRequest(request);

	}

	public String getHookConfig(String version){

		HttpUriRequest request=getBuilder().getHookConfig(version).generRequest(mapper);

		return client.executeRequest(request);


	}

	public String getCurrentVersion(){

		HttpUriRequest request=getBuilder().getCurrentVersion().generRequest(mapper);

		return client.executeRequest(request);
	}

	public String getSystemParameter(String name){

		HttpUriRequest  request=getBuilder()
				.getSystemParameter(name)
				.generRequest(mapper);

		try {
			return client.executeRequest(request);
		}catch(AppParameterCodeNotFoundException e){
			return null;
		}
	}


	public void setSystemParameter(String name,String value){

		HttpUriRequest  request=getBuilder()
				.setSystemParameter(name,value)
				.generRequest(mapper);

		client.executeRequest(request);
	}


	public void deployServiceExtension(String serviceCtx,String hookDescription){

		final AppInfo appInfo=bindToolResolver.getAppInfo();

		HttpUriRequest request=getBuilder().deployServiceCode(serviceCtx).generRequest(mapper);

		String adminToken=bindToolResolver.getToken();

		client.asyncExecuteRequest(request, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse httpResponse) {

				int status=httpResponse.getStatusLine().getStatusCode();
				if(status>=200&&status<300){
					throw new IllegalArgumentException();
				}

				bindToolResolver.setAppInfoDirectly(appInfo,adminToken);

				String response= HttpUtils.getResponseBody(httpResponse);

				Map<String,Object> result= null;
				try {
					result = mapper.readValue(response,Map.class);
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
				String version= (String) result.get("versionID");

				HttpUriRequest hookRequest=getBuilder().deployHook(hookDescription,version).generRequest(mapper);

				client.asyncExecuteRequest(hookRequest, new FutureCallback<HttpResponse>() {
					@Override
					public void completed(HttpResponse httpResponse) {
						bindToolResolver.setAppInfoDirectly(appInfo,adminToken);

						HttpUriRequest setVerRequest=getBuilder().setCurrentVersion(version).generRequest(mapper);

						client.asyncExecuteRequest(setVerRequest, new FutureCallback<HttpResponse>() {
							@Override
							public void completed(HttpResponse httpResponse) {
								log.info("finish ");
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

			@Override
			public void failed(Exception e) {

			}

			@Override
			public void cancelled() {

			}
		});

	}
}
