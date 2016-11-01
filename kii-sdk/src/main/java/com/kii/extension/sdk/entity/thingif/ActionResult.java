package com.kii.extension.sdk.entity.thingif;


public class ActionResult {


	/*
	   	{"turnPower":{"result":true,"errorMessage":"","transaction":{"voltage":"125"}},

	 */
	private boolean succeeded;

	private String errorMessage;

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
