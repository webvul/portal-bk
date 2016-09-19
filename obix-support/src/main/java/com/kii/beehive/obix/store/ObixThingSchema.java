package com.kii.beehive.obix.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kii.beehive.obix.store.beehive.ActionInput;
import com.kii.beehive.obix.store.beehive.PointDetail;
import com.kii.beehive.obix.store.beehive.ThingSchema;
import com.kii.extension.sdk.entity.KiiEntity;

public class ObixThingSchema extends KiiEntity{





	private String framewireVersion;

	private String description;

	private Map<String,ObixPointDetail>  fieldCollect=new HashMap<>();

	private Map<String,Boolean> tagCollect=new HashMap<>();

	private String name;

	private String superRef;



	public  ObixThingSchema(ThingSchema th)  {

		name=th.getName();

		th.getStatesSchema().getProperties().forEach((k,v)->{

			ObixPointDetail point=new ObixPointDetail(k,v);

			point.setExistCur(true);

			addField(point);
		});

		th.getActions().forEach((k,v)->{

			ActionInput in=v.getIn();

			Map<String,PointDetail> pointMap=in.getProperties();

			if(pointMap.size()>1||pointMap.size()==0){
				return;
			}
			Map.Entry<String,PointDetail> entry=pointMap.entrySet().iterator().next();

			PointDetail detail=entry.getValue();
			String fieldName=entry.getKey();

			ObixPointDetail point=getFieldCollect().get(fieldName);

			if(point==null){
				point=new ObixPointDetail(fieldName,detail);
			}

			point.setWritable(true);

			addField(point);
		});

	}




	public String getSuperRef() {
		return superRef;
	}

	public void setSuperRef(String superRef) {
		this.superRef = superRef;
	}

//	public String getFullSchemaName(){
//		return name+"_"+framewireVersion;
//	}
//
//	public void setFullSchemaName(){
//
//	}

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

	public Map<String, ObixPointDetail> getFieldCollect() {
		return fieldCollect;
	}

	public void setFieldCollect(Map<String, ObixPointDetail> fieldCollect) {
		this.fieldCollect = fieldCollect;
	}

	public void addField(ObixPointDetail detail){

		this.fieldCollect.put(detail.getFieldName(),detail);
	}

	public void addFieldList(List<ObixPointDetail>  details){
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
