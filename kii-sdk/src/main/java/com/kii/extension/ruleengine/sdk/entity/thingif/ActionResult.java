package com.kii.extension.ruleengine.sdk.entity.thingif;


public class ActionResult {


	/*
	   	{"turnPower":{"result":true,"errorMessage":"","transaction":{"voltage":"125"}},

	 */
	private boolean succeeded;

	private String result;

	private String errorMessage;

	private Action data;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Action getData() {
		return data;
	}

	public void setData(Action data) {
		this.data = data;
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}
}
