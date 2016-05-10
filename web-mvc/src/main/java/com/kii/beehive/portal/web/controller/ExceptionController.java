package com.kii.beehive.portal.web.controller;

import javax.annotation.PostConstruct;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.exception.StoreServiceException;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.extension.sdk.exception.KiiCloudException;

@ControllerAdvice
public class ExceptionController {

	private Logger log = LoggerFactory.getLogger(ExceptionController.class);

	@Autowired
	private ObjectMapper mapper;

	private MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();

	@PostConstruct
	public void init(){

//		headers.add("Content-Type","application/json;charset=UTF-8");

	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<String> handleGlobalException(Throwable ex) {

		log.error("global exception ", ex);

//		String error=ex.getErrorCode().toString();

		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("errorCode", ex.getClass().getSimpleName());
		errorMap.put("errorMessage", ex.getMessage());

//		String errJson=mapper.writeValueAsString(errorMap);

		ResponseEntity<String> resp = new ResponseEntity(errorMap, headers,HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}

	private List<String> filter = CollectUtils.createList("status", "suppressed", "stackTrace", "class", "localizedMessage");

	private String convertExeptionToJson(RuntimeException ex) {

		Map<String, Object> error = new HashMap<>();

		for (PropertyDescriptor desc : BeanUtils.getPropertyDescriptors(ex.getClass())) {

			if (!filter.contains(desc.getDisplayName())) {
				try {
					Method method = desc.getReadMethod();
					if (method.isAnnotationPresent(JsonIgnore.class)) {
						continue;
					}
					Object val = desc.getReadMethod().invoke(ex, null);
					if (val == null) {
						continue;
					}
					error.put(desc.getDisplayName(), val);
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.error("exception convert to json fail", e);
					throw new IllegalArgumentException(e);
				}
			}
		}

		try {
			return mapper.writeValueAsString(error);
		} catch (JsonProcessingException e) {
			log.error("exception convert to json fail", e);
			throw new IllegalArgumentException(e);
		}
	}


	@ExceptionHandler(StoreServiceException.class)
	public ResponseEntity<String> handleStoreServiceException(StoreServiceException ex) {

		log.error("store exception ", ex);

		String error = convertExeptionToJson(ex);

		ResponseEntity<String> resp = new ResponseEntity(error,headers, HttpStatus.valueOf(ex.getStatusCode()));


		return resp;
	}

	@ExceptionHandler(PortalException.class)
	public ResponseEntity<String> handleServiceException(PortalException ex) {

		log.error("portal exception ", ex);

		String error = convertExeptionToJson(ex);

		ResponseEntity<String> resp = new ResponseEntity(error,headers, ex.getStatus());
		return resp;
	}

	@ExceptionHandler(KiiCloudException.class)
	public ResponseEntity<String> handleKiiCloudException(KiiCloudException ex) {

		log.error("kiicloud exception ", ex);

		String error = convertExeptionToJson(ex);

		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());

		ResponseEntity<String> resp = new ResponseEntity(error,headers, status);
		return resp;
	}
}
