package com.kii.beehive.portal.store.entity;

import java.util.Map;

public class MLTaskDetail extends PortalEntity {
	
	private String mlTaskID;
	
	private Map<String,Object> mlOutput;
	
	private int interval;

	private  Map<String,Object>  schema;
	
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
		return mlTaskID;
	}
	
	public void setMlTaskID(String mlTaskID) {
		this.mlTaskID = mlTaskID;
	}
	
//	public enum Status{
//		enable,disable,deleted;
//	}
}
