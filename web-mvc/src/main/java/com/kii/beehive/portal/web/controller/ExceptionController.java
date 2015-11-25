package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.web.help.PortalException;
import com.kii.extension.sdk.exception.KiiCloudException;

@ControllerAdvice
public class ExceptionController {

	private Logger log= LoggerFactory.getLogger(ExceptionController.class);

	@Autowired
	private ObjectMapper mapper;

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<String> handleGlobalException(Throwable ex) {

		log.error("global exception ",ex);

//		String error=ex.getErrorCode().toString();

		Map<String,String> errorMap=new HashMap<>();
		errorMap.put("errorCode",ex.getClass().getSimpleName());
		errorMap.put("errorMessage",ex.getMessage());

//		String errJson=mapper.writeValueAsString(errorMap);

		ResponseEntity<String> resp=new ResponseEntity(errorMap,HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}

	@ExceptionHandler(StoreException.class)
	public ResponseEntity<String> handleStoreServiceException(StoreException ex) {

		log.error("store exception ",ex);

		String error= null;
		try {
			error = mapper.writeValueAsString(ex);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

		ResponseEntity<String> resp=new ResponseEntity(error,HttpStatus.valueOf(ex.getStatusCode()));

		return resp;
	}

	@ExceptionHandler(PortalException.class)
	public ResponseEntity<String> handleServiceException(PortalException ex) {

		log.error("portal exception ",ex);


		String error=ex.getErrorCode().toString();

		ResponseEntity<String> resp=new ResponseEntity(error,ex.getHttpStatus());
		return resp;
	}

	@ExceptionHandler(KiiCloudException.class)
	public ResponseEntity<String> handleKiiCloudException(KiiCloudException ex) {

		log.error("kiicloud exception ",ex);

		String error= null;
		try {
			error = mapper.writeValueAsString(ex);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

		HttpStatus status=HttpStatus.valueOf(ex.getStatusCode());

		ResponseEntity<String> resp=new ResponseEntity(error,status);
		return resp;
	}
}
