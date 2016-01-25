package com.kii.beehive.portal.event;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.KiiEntity;

public class EventListener extends KiiEntity {


	private EventType type;

	private Map<String,Boolean> bindKeyMap=new HashMap<>();

	private String targetKey;

	private String relationBeanName;


	private boolean enable;

	public boolean isEnable() {
		return enable;
	}


	private Map<String,Object> customs=new HashMap<>();

	@JsonIgnore
	public void addCustomValue(String field,Object value){
		customs.put(field,value);
	}

	public Map<String, Object> getCustoms() {
		return customs;
	}

	public void setCustoms(Map<String, Object> customs) {
		this.customs = customs;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}



	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Map<String,Boolean> getBindKeys() {
		return bindKeyMap;
	}

	public void setBindKeys(Map<String,Boolean> bindKeyMap) {
		this.bindKeyMap = bindKeyMap;
	}

	@JsonIgnore
	public void addBindKeys(Collection<String> bindKeys) {
		bindKeys.forEach(key->{
			bindKeyMap.put(key,true);
		});
	}

	@JsonIgnore
	public void addBindKey(String bindKey){
		bindKeyMap.put(bindKey,true);
	}

	public String getTargetKey() {
		return targetKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	public String getRelationBeanName() {
		return relationBeanName;
	}


	public void setRelationBeanName(String relationBeanName) {
		this.relationBeanName = relationBeanName;
	}

}
