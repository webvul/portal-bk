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

	/**
	 * deploy server code on app, below 3 steps will be done in different threads
	 * 1. upload server code file
	 * 2. upload hook file
	 * 3. set server code version (activate server code)
	 *
	 * @param serviceCtx
	 * @param hookDescription
	 */
	public void deployServiceExtension(String serviceCtx,String hookDescription){

		final AppInfo appInfo=bindToolResolver.getAppInfo();

		log.info("deployServiceExtension start on app " + appInfo.getAppID());

		HttpUriRequest request=getBuilder().deployServiceCode(serviceCtx).generRequest(mapper);

		String adminToken=bindToolResolver.getToken();

		client.asyncExecuteRequest(request, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse httpResponse) {

				checkResponseCode(httpResponse);

				log.info("deploy server code file completed on app " + appInfo.getAppID());

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

						checkResponseCode(httpResponse);

						log.info("deploy hook file completed on app " + appInfo.getAppID());

						bindToolResolver.setAppInfoDirectly(appInfo,adminToken);

						HttpUriRequest setVerRequest=getBuilder().setCurrentVersion(version).generRequest(mapper);

						client.asyncExecuteRequest(setVerRequest, new FutureCallback<HttpResponse>() {
							@Override
							public void completed(HttpResponse httpResponse) {

								checkResponseCode(httpResponse);

								log.info("set server code version completed on app " + appInfo.getAppID());
								log.info("deployServiceExtension end on app " + appInfo.getAppID());
							}

							@Override
							public void failed(Exception e) {
								log.error("set server code version failed on app " + appInfo.getAppID(), e);
							}

							@Override
							public void cancelled() {
								log.info("set server code version cancelled on app " + appInfo.getAppID());
							}
						});
					}

					@Override
					public void failed(Exception e) {
						log.error("deploy hook file failed on app " + appInfo.getAppID(), e);
					}

					@Override
					public void cancelled() {
						log.info("deploy hook file cancelled on app " + appInfo.getAppID());
					}
				});
			}

			@Override
			public void failed(Exception e) {
				log.error("deploy server code file failed on app " + appInfo.getAppID(), e);
			}

			@Override
			public void cancelled() {
				log.info("deploy server code file cancelled on app " + appInfo.getAppID());
			}
		});

	}

	private void checkResponseCode(HttpResponse httpResponse) {
		int status=httpResponse.getStatusLine().getStatusCode();
		if(status < 200 || status >= 300){
			throw new IllegalArgumentException("http status " + status + " is returned");
		}
	}

	/**
	 * deploy server code on app, below 3 steps will be done in same thread
	 * 1. upload server code file
	 * 2. upload hook file
	 * 3. set server code version (activate server code)
	 *
	 * * the function of this method is the same with below method
	 * 	- public void deployServiceExtension(String serviceCtx,String hookDescription)
	 *
	 * @param serviceCtx
	 * @param hookDescription
     */
	public void deployServiceExtensionSync(String serviceCtx,String hookDescription){

		final AppInfo appInfo=bindToolResolver.getAppInfo();

		log.info("deployServiceExtension start on app " + appInfo.getAppID());

		// deploy server code file
		log.info("deploy server code file on app " + appInfo.getAppID());

		HttpUriRequest request=getBuilder().deployServiceCode(serviceCtx).generRequest(mapper);
		String response = client.executeRequest(request);

		Map<String,Object> result= null;
		try {
			result = mapper.readValue(response,Map.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		String version= (String) result.get("versionID");
		log.info("deploy server code file on app " + appInfo.getAppID() + " and get version ID " + version);

		// deploy hook file
		log.info("deploy hook file on app " + appInfo.getAppID());

		HttpUriRequest hookRequest=getBuilder().deployHook(hookDescription,version).generRequest(mapper);
		client.doRequest(hookRequest);

		// set server code version
		log.info("set server code version on app " + appInfo.getAppID());

		HttpUriRequest setVerRequest=getBuilder().setCurrentVersion(version).generRequest(mapper);
		client.doRequest(setVerRequest);

		log.info("deployServiceExtension end on app " + appInfo.getAppID());

	}
}
