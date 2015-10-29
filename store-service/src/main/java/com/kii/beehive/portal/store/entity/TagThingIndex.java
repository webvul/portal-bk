package com.kii.beehive.portal.store.entity;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TagThingIndex {



	private String tagName;


	private Set<String> globalThings;

	private Set<String> appIDs;


}
