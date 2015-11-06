package com.kii.beehive.portal.store.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import com.kii.extension.sdk.entity.KiiEntity;


public class TagIndex extends KiiEntity {



	private String tagName;


	private Set<String> globalThings=new HashSet<>();

	private Set<String> appIDs=new HashSet<>();

	@Override
	public String getId() {
		return tagName;
	}

	@Override
	public void setId(String tagName) {
		this.tagName = tagName;
	}

	public Set<String> getGlobalThings() {
		return globalThings;
	}

	public void setGlobalThings(Set<String> globalThings) {
		this.globalThings = globalThings;
	}

	public Set<String> getAppIDs() {
		return appIDs;
	}

	public void setAppIDs(Set<String> appIDs) {
		this.appIDs = appIDs;
	}
}
