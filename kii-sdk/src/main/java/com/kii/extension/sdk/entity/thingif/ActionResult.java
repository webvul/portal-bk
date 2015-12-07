package com.kii.extension.sdk.entity.thingif;


public class ActionResult {


	/*
	   	{"turnPower":{"result":true,"errorMessage":"","data":{"voltage":"125"}},

	 */

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
}
