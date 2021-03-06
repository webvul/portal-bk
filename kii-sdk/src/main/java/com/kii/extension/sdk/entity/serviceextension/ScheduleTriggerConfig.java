package com.kii.extension.sdk.entity.serviceextension;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ScheduleTriggerConfig {

	/*
	      "cron": "<cron_expression>",
      "endpoint": "<endpoint_name>",
      "parameters": {
        "arg1": "xxxx"
      },
      "what": "EXECUTE_SERVER_CODE"

	 */

	private TriggerWhatType what=TriggerWhatType.EXECUTE_SERVER_CODE;

	private Map<String,Object> parameters=new HashMap<>();

	private String endpoint;

	private String cron;




	@JsonIgnore
	public String   getJobName(){
		String jobName=endpoint+"_"+ StringUtils.trimAllWhitespace(cron);

		return jobName;
	}



	public TriggerWhatType getWhat() {
		return what;
	}

	public void setWhat(TriggerWhatType what) {
		this.what = what;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
