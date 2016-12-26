package com.kii.extension.sdk.entity.thingif;


public class ActionResult {


	/*
	   	{"turnPower":{"task":true,"errorMessage":"","transaction":{"voltage":"125"}},

	 */
	
	private ThingStatus data;
	
	private boolean succeeded;

	private String errorMessage;
	
	public ThingStatus getData() {
		return data;
	}
	
	public void setData(ThingStatus data) {
		this.data = data;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}
}
