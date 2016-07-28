package com.kii.extension.ruleengine.store.trigger.result;

import java.util.HashMap;
import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class HttpCallResponse extends KiiEntity implements TriggerResult{

	private int status;

	private String body;

	private Map<String,String> headers=new HashMap<>();

	private String triggerID;

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

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

	@Override
	public String getType() {
		return "httpResponse";
	}
}
