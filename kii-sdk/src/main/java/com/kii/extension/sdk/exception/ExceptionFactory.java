package com.kii.extension.sdk.exception;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;

@Component
public class ExceptionFactory {

	@Autowired
	private ObjectMapper mapper;

	private Map<Integer,Class<? extends KiiCloudException>> clsMap=new HashMap<>();


	@PostConstruct
	public void init(){

		clsMap.put(400,InvalidBucketException.class);
		clsMap.put(401,UnauthorizedAccessException.class);
		clsMap.put(409,StaleVersionedObjectException.class);
		clsMap.put(404,ObjectNotFoundException.class);

	}

	public void checkResponse(HttpResponse response) throws KiiCloudException {

		int status=response.getStatusLine().getStatusCode();

		if(status>200&&status<300){

			return;
		}

		if(status>=400){

			String body= HttpUtils.getResponseBody(response);

			Class<? extends KiiCloudException> cls=clsMap.get(status);

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
