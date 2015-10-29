package com.kii.beehive.portal.store.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndex(name="location_idx",def="{'blockNo':1,'levelNo':1,'type':1}")
public class Location {

	@Id
	private String id;


	private String fullLocation;

	private String blockNo;

	private String levelNo;

	private String type;

	private String number;


	private Set<String> nearLocations;



}
