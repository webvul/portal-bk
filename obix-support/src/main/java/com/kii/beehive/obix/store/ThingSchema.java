package com.kii.beehive.obix.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class ThingSchema extends KiiEntity{





	private String framewireVersion;

	private String description;

	private Map<String,PointDetail>  fieldCollect=new HashMap<>();

	private Map<String,Boolean> tagCollect=new HashMap<>();

	private String name;

	private String superRef;


	public String getSuperRef() {
		return superRef;
	}

	public void setSuperRef(String superRef) {
		this.superRef = superRef;
	}

	public String getFullSchemaName(){
		return name+"_"+framewireVersion;
	}

	public void setFullSchemaName(){

	}

	public String getFramewireVersion() {
		return framewireVersion;
	}

	public void setFramewireVersion(String version) {
		this.framewireVersion = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, PointDetail> getFieldCollect() {
		return fieldCollect;
	}

	public void setFieldCollect(Map<String, PointDetail> fieldCollect) {
		this.fieldCollect = fieldCollect;
	}

	public void addField(PointDetail  detail){

		this.fieldCollect.put(detail.getFieldName(),detail);
	}

	public void addFieldList(List<PointDetail>  details){
		details.forEach(this::addField);
	}

	public Map<String, Boolean> getTagCollect() {
		return tagCollect;
	}

	public void setTagCollect(Map<String, Boolean> tagCollect) {
		this.tagCollect = tagCollect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addTag(String tag){
		tagCollect.put(tag,true);
	}

	public void addTagSet(List<String> tags){

		tags.forEach(this::addTag);
	}
}
