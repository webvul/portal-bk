package com.kii.beehive.portal.exception;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BusinessException extends RuntimeException {


	private Map<String,String> paramMap=new HashMap<>();


	private String errorCode;

	private int statusCode;


	public String getErrorCode() {

		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void addParam(String key,String value){
		paramMap.put(key,value);
	}

	public Map<String,String> getParamMap(){
		return paramMap;
	}


	@JsonIgnore
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();

	}
}