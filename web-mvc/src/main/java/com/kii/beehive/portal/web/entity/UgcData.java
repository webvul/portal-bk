package com.kii.beehive.portal.web.entity;

import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.UserGeneratedContent;

public class UgcData {


	private String name;

	private CustomData  data;

	public UgcData(){

	}

	public UgcData(UserGeneratedContent  ugc){
		this.name=ugc.getName();
		this.data=ugc.getUserData();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public CustomData getData() {
		return data;
	}

	public void setData(CustomData data) {
		this.data = data;
	}
}
