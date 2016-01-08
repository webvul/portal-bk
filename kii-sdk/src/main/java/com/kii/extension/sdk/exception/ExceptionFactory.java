package com.kii.extension.sdk.exception;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;

@Component
public class ExceptionFactory {

	private Logger log= LoggerFactory.getLogger(ExceptionFactory.class);

	@Autowired
	private ObjectMapper mapper;

	private Map<Integer,Class<? extends KiiCloudException>> bucketClsMap =new HashMap<>();

	private Map<Integer,Class<? extends KiiCloudException>> userClsMap =new HashMap<>();

	private Map<Integer,Class<? extends KiiCloudException>> appClsMap =new HashMap<>();


	@PostConstruct
	public void init(){

		bucketClsMap.put(400, InvalidBucketException.class);
		bucketClsMap.put(401, UnauthorizedAccessException.class);
		bucketClsMap.put(409, StaleVersionedObjectException.class);
		bucketClsMap.put(404, ObjectNotFoundException.class);
		bucketClsMap.put(403, ForbiddenException.class);

		userClsMap.put(409, UserAlreadyExistsException.class);
		userClsMap.put(400,BadUserNameException.class);
		userClsMap.put(404,UserNotFoundException.class);
		userClsMap.put(401, UnauthorizedAccessException.class);
		userClsMap.put(403, ForbiddenException.class);

		appClsMap.put(404,AppParameterCodeNotFoundException.class);

	}

	public enum OperateType{
		bucket,user,app;
	}

	public OperateType getOperateType(URI url){

		if(url.getPath().contains("/buckets/")){
			return OperateType.bucket;
		}else if(url.getPath().contains("/users")){
			return OperateType.user;
		}else if(url.getPath().contains("/configuration/")){
			return OperateType.app;
		}else{
			return OperateType.bucket;
		}

	}

	public void checkResponse(HttpResponse response,URI uri)throws KiiCloudException{

		OperateType type=getOperateType(uri);

		Map<Integer,Class<? extends KiiCloudException>> map=null;
		switch(type){
			case bucket:
				map=bucketClsMap;
				break;
			case user:
				map=userClsMap;
				break;
			case app:
				map=appClsMap;
				break;
		}
		checkResponse(response, map);

	}



	private void checkResponse(HttpResponse response,Map<Integer,Class<? extends KiiCloudException>> excepMap) throws KiiCloudException {

		int status=response.getStatusLine().getStatusCode();

		if(status>=200&&status<300){

			return;
		}

		if(status>=400){

			String body= HttpUtils.getResponseBody(response);
			log.error("Http Code: " + status + ", Response Body: " + body);
			Class<? extends KiiCloudException> cls= excepMap.get(status);

			if(cls!=null){
				try {
					KiiCloudException e = mapper.readValue(body, cls);

					throw e;
				}catch(IOException ex){
					throw new IllegalArgumentException(ex);
				}
			}
		}
	}



}
