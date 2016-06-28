package com.kii.beehive.business.entity;


import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagSelector {

	private List<Long> thingList=new ArrayList<>();

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

	public void setTagCollect(List<String> tagCollect) {
		this.tagList = tagCollect;
	}

	public boolean isAndExpress() {
		return isAndExpress;
	}

	public void setAndExpress(boolean andExpress) {
		isAndExpress = andExpress;
	}


	public List<Long> getThingList() {
		return thingList;
	}

	public void setThingList(List<Long> thingList) {
		this.thingList = thingList;
	}

	@JsonIgnore
	public void addTag(String tagName) {
		this.tagList.add(tagName);
	}
}
