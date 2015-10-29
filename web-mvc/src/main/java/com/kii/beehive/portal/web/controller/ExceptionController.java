package com.kii.beehive.portal.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.kii.beehive.portal.common.exception.PortalException;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(PortalException.class)
	public ResponseEntity<String> handleServiceException(PortalException ex) {


		String error=ex.getErrorCode().toString();

		ResponseEntity<String> resp=new ResponseEntity(error,ex.getHttpStatus());
		return resp;
	}
}
