package com.kii.beehive.obix.store;

import java.util.HashSet;
import java.util.Set;

import com.kii.beehive.obix.store.beehive.ThingSchema;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class ThingInfo {

	private Set<PointInfo> pointCollect=new HashSet<>();

	private String name;

	private ObixThingSchema schema;

	private String location;

	private Set<String> customTags=new HashSet<>();


	public ThingInfo(){

	}

	public ThingInfo(ThingSchema thSchema, ThingStatus status){

		this.schema=new ObixThingSchema(thSchema);

		schema.getFieldCollect().forEach((k,v)->{

			PointInfo  point=new PointInfo(v,k,status.getField(k));


			pointCollect.add(point);

		});



	}

	public void initLocation( String location){

		this.location=location;

		int i=0;
		for(PointInfo  p:pointCollect){
			p.setLocation(location+"-0"+i);
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Set<String> getCustomTags() {
		return customTags;
	}

	public void setCustomTags(Set<String> customTags) {
		this.customTags = customTags;
	}

	public Set<PointInfo> getPointCollect() {
		return pointCollect;
	}

	public void setPointCollect(Set<PointInfo> pointCollect) {
		this.pointCollect = pointCollect;
	}

	public ObixThingSchema getSchema() {
		return schema;
	}

	public void setSchema(ObixThingSchema schema) {
		this.schema = schema;
	}
}
