package com.kii.beehive.portal.store.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CustomProperty implements Serializable{

	private Map<String,Object> customFields=new HashMap<>();

	public CustomProperty(){

	}

	public CustomProperty(Map<String,Object> param){
		this.customFields.putAll(param);
	}

	@JsonIgnore
	public Map<String,Object> getOriginFields(){
		return customFields;
	}

	@JsonAnyGetter
	public Map<String,Object> getCustomField(){

		Map<String,Object> map=new HashMap<>();
		customFields.forEach((k,v)->{
			map.put("custom-"+k,v);
		});
		return map;
	}

	@JsonAnySetter
	public void setCustomField(String key,Object value){
		if(key.startsWith("custom-")){
			key=key.substring(7,key.length());
		}
		this.customFields.put(key, value);
	}

	@JsonIgnore
	public Object getValueByKey(String key){
		return customFields.get(key);
	}

	@JsonIgnore
	public Map<String,Object> filter(Set<String> filter){
		Map<String,Object> map=new HashMap<>(customFields);

		map.keySet().removeIf((k) -> !filter.contains(k));

		return map;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return customFields.isEmpty();
	}

	@JsonIgnore
	public void join(CustomProperty customFields) {
		customFields.customFields.putAll(this.customFields);
		this.customFields=customFields.customFields;
	}
}
