package com.kii.extension.sdk.entity.serviceextension;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.common.utils.StrTemplate;

public class EventTriggerConfig {


	private WhenType when;


	private String endpoint;

	private String url;


	private TriggerWhatType what=TriggerWhatType.EXECUTE_SERVER_CODE;


	public TriggerWhatType getWhat() {
		return what;
	}

	public void setWhat(TriggerWhatType what) {
		this.what = what;
	}



	public String  getUrl(){

		return url;
	}

	public void setUrl(String url){
		this.url=url;
	}

	public WhenType getWhen() {
		return when;
	}

	public void setWhen(WhenType when) {
		this.when = when;
	}



	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}
