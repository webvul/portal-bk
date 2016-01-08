package com.kii.beehive.portal.event;

import java.util.HashMap;
import java.util.Map;


public class EventParam {


	private Map<String,Object> paramMap=new HashMap<>();

	public void setParam(String key,Object value) {
		this.paramMap.put(key,value);
	}

	public Object getParam(String key){

		return paramMap.get(key);
	}
}
