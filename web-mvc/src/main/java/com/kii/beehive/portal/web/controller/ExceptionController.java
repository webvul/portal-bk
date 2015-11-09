package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.web.help.PortalException;
import com.kii.extension.sdk.exception.KiiCloudException;

@ControllerAdvice
public class ExceptionController {

	@Autowired
	private ObjectMapper mapper;

	@ExceptionHandler(PortalException.class)
	public ResponseEntity<String> handleServiceException(PortalException ex) {


		String error=ex.getErrorCode().toString();

		ResponseEntity<String> resp=new ResponseEntity(error,ex.getHttpStatus());
		return resp;
	}

	@ExceptionHandler(KiiCloudException.class)
	public ResponseEntity<String> handleKiiCloudException(KiiCloudException ex) {


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
