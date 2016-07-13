package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class CustomData {


	private Map<String,Object> data=new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> getData() {
		return data;
	}


	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@JsonAnySetter
	public void addData(String key,Object value){
		data.put(key,value);
	}
}
