package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;

public class MLTaskDetail extends PortalEntity {
	
	
	private Map<String,Object> mlOutput=new HashMap<>();
	
	private int interval;

	private  Map<String,Object>  schema=new HashMap<>();
	
	private String accessUrl;
	
	private MLTaskErrorInfo lastError;

	
	public MLTaskErrorInfo getLastError() {
		return lastError;
	}
	
	public void setLastError(MLTaskErrorInfo lastError) {
		this.lastError = lastError;
	}
	
	public String getAccessUrl() {
		return accessUrl;
	}
	
	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}
	
	public Map<String, Object> getSchema() {
		return schema;
	}
	
	public void setSchema(Map<String, Object> schema) {
		this.schema = schema;
	}
	
	//schema
	
	
	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public Map<String, Object> getMlOutput() {
		return mlOutput;
	}
	
	public void setMlOutput(Map<String, Object> mlOutput) {
		this.mlOutput = mlOutput;
	}
	
	public String getMlTaskID() {
		return super.getId();
	}
	
	public void setMlTaskID(String mlTaskID) {
		super.setId(mlTaskID);
	}
	
//	public enum Status{
//		enable,disable,deleted;
//	}
}
