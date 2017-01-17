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
import org.springframework.util.StringUtils;

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


	Class[] thingArray={


	};

	Class[] thingIFArray={


	};

	Class[] installationArray={
			MQTTNotReadyException.class
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

		this.initExceptionMap(installationArray,OperateType.installation);

		this.initExceptionMap(thingArray, OperateType.thing);

		this.initExceptionMap(thingIFArray, OperateType.thingif);


	}

	public enum OperateType{
		bucket,user,app,thing,thingif,installation;

		public static  OperateType getInstance(URI url){

			if(url.getPath().contains("/buckets/")){
				return OperateType.bucket;
			}else if(url.getPath().contains("/users") || url.getPath().contains("/oauth2")){
				return OperateType.user;
			}else if(url.getPath().contains("/configuration/")){
				return OperateType.app;
			}else if(url.getPath().contains("/installation/")){
				return OperateType.installation;
			}else if(url.getPath().contains("/things/")){
				return OperateType.thing;
			}else if(url.getPath().contains("/thing-if/")){
				return OperateType.thingif;
			}else{
				return OperateType.bucket;
			}

		}
	}



	public void checkResponse(HttpResponse response,URI uri)throws KiiCloudException{

		int status=response.getStatusLine().getStatusCode();
		if(status>=200&&status<300){
			return;
		}

		OperateType type=OperateType.getInstance(uri);

		Map<Integer,Class<? extends KiiCloudException>> map=exceptionMap.getOrDefault(type,new HashMap<>());

		checkResponse(response, map);

	}



	private void checkResponse(HttpResponse response,Map<Integer,Class<? extends KiiCloudException>> excepMap) throws KiiCloudException {

		int status=response.getStatusLine().getStatusCode();

		if(status>=200&&status<300){

			return;
		}
		
		String body= HttpUtils.getResponseBody(response);
		
		if(status>=400&&status<500){

			log.error("Http Code: " + status + ", Response Body: " + body);
			Class<? extends KiiCloudException> cls= excepMap.get(status);

			if(cls!=null){
				try {
					if(StringUtils.isEmpty(body)){

						KiiCloudException e =BeanUtils.instantiate(cls);
						throw e;

					}else {
						KiiCloudException e = mapper.readValue(body, cls);

						throw e;
					}
				}catch(IOException ex){
					throw new IllegalArgumentException(ex);
				}
			}else{
				throw new IllegalArgumentException("error code:"+status);
			}
		}
		
		if(status>=500){
			throw new SystemException(response);
		}
	}



}
