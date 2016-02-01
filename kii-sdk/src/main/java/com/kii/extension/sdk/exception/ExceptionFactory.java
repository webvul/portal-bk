package com.kii.extension.sdk.exception;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;

@Component
public class ExceptionFactory {

	private Logger log= LoggerFactory.getLogger(ExceptionFactory.class);

	@Autowired
	private ObjectMapper mapper;

	private Map<OperateType,Map<Integer,Class<? extends KiiCloudException>>> exceptionMap=new HashMap<>();


	Class[]  bucketArray={
			InvalidBucketException.class,
			UnauthorizedAccessException.class,
			StaleVersionedObjectException.class,
			ObjectNotFoundException.class,
			ForbiddenException.class
	};

	Class[]  userArray={
			UserAlreadyExistsException.class,
			BadUserNameException.class,
			UserNotFoundException.class,
			UnauthorizedAccessException.class,
			ForbiddenException.class
	};

	Class[]  appArray={
			AppParameterCodeNotFoundException.class
	};

	private void initExceptionMap(Class[] exceptionClassArray, OperateType operateType) {
		Map map = new HashMap<>();

		for(Class<KiiCloudException>  ex : exceptionClassArray){

			KiiCloudException  inst = BeanUtils.instantiate(ex);
			int statusCode = inst.getStatusCode();
			map.put(statusCode, ex);
		}

		exceptionMap.put(operateType, map);
	}

	@PostConstruct
	public void init(){

		this.initExceptionMap(bucketArray, OperateType.bucket);

		this.initExceptionMap(userArray, OperateType.user);

		this.initExceptionMap(appArray, OperateType.app);

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

		Map<Integer,Class<? extends KiiCloudException>> map=exceptionMap.getOrDefault(type,new HashMap<>());

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
			}else{
				throw new IllegalArgumentException("error code:"+status);
			}
		}
	}



}
