package com.kii.beehive.business.ruleengine;

import java.util.Map;

public class TriggerOperateException extends TriggerException {
	
	private String errorMessage;
	
	public TriggerOperateException(Map<String,Object> errInfo, int status){
		
		
		super.setErrorCode((String) errInfo.get("errorCode"));
		
		this.errorMessage= (String) errInfo.get("errorMessage");
		
		super.setStatusCode(status);
		
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
