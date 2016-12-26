package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagSelector {

	
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	
	private List<String> tagList=new ArrayList<>();

	private boolean isAndExpress=false;

	private String type;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTagList() {
		return tagList;
	}


	public boolean isAndExpress() {
		return isAndExpress;
	}

	public void setAndExpress(boolean andExpress) {
		isAndExpress = andExpress;
	}



	@JsonIgnore
	public void addTag(String tagName) {
		this.tagList.add(tagName);
	}
	
	
	public boolean notEmpty() {
		
		return !tagList.isEmpty();
	}
}
