package com.kii.beehive.business.entity;

import java.util.Date;

import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class ESThingStatus {
	
	private ThingStatus status;
	
	private Date uploadTime;
	
	public ESThingStatus(){
		
	}
	
	public ESThingStatus(ThingStatus status){
		this.status=status;
		this.uploadTime=new Date();
	}
	
	public ThingStatus getStatus() {
		return status;
	}
	
	public void setStatus(ThingStatus status) {
		this.status = status;
	}
	
	public Date getUploadTime() {
		return uploadTime;
	}
	
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
}
