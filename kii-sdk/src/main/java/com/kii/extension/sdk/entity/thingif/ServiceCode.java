package com.kii.extension.sdk.entity.thingif;

import java.util.HashMap;
import java.util.Map;

public class ServiceCode {
/*
  endpoint: saveTemperature
  parameters:
    temperature: 60
  targetAppID: nfoi2oi
  executorAccessToken: xxxxxx
 */

	private String endpoint;

	private Map<String,Object> parameters=new HashMap<>();

	private String targetAppID;

	private String executorAccessToken;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getTargetAppID() {
		return targetAppID;
	}

	public void setTargetAppID(String targetAppID) {
		this.targetAppID = targetAppID;
	}

	public String getExecutorAccessToken() {
		return executorAccessToken;
	}

	public void setExecutorAccessToken(String executorAccessToken) {
		this.executorAccessToken = executorAccessToken;
	}
}
