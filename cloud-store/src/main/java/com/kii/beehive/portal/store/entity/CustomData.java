package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CustomData {


	private Map<String,Object> data=new HashMap<>();

	@JsonAnySetter
	public Map<String, Object> getData() {
		return data;
	}


	@JsonIgnore
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@JsonAnySetter
	public void addData(String key,Object value){
		data.put(key,value);
	}
}
