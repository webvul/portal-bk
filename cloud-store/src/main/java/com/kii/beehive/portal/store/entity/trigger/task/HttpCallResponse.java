package com.kii.beehive.portal.store.entity.trigger.task;

import java.util.HashMap;
import java.util.Map;

public class HttpCallResponse extends  TriggerResult{

	private int status;

	private String body;

	private CallHttpApi  httpRequest;


	private Map<String,String> headers=new HashMap<>();



	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void addHeader(String name, String value) {
		headers.put(name,value);
	}

	public CallHttpApi getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(CallHttpApi httpRequest) {
		this.httpRequest = httpRequest;
	}

	@Override
	public String getType() {
		return "httpResponse";
	}


}
