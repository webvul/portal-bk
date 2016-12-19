package com.kii.beehive.business.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class BulkOperate {
	
	/*
	{ "index":{ "_index":"roles","_type":"default" } }
{ "name":"zhangShan","role":["a","b","c"]}
{ "index":{ "_index":"roles","_type":"default"} }
{ "name":"lishi","role":["a","b"]}
{ "index" : { "_index" : "roles","_type":"default"} }
{ "name" : "wanger","role":["a"]}
	 */
	
	private OperateType operate;
	
	private ESIndex  index;
	
	private String id;
	
	private Object data;
	
	private String parent;
	
	@JsonIgnore
	public OperateType getOperate() {
		return operate;
	}
	
	public void setOperate(OperateType operate) {
		this.operate = operate;
	}
	
	@JsonUnwrapped
	public ESIndex getIndex() {
		return index;
	}
	
	public void setIndex(ESIndex index) {
		this.index = index;
	}
	
	@JsonProperty(value="_id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonIgnore
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	@JsonProperty(value="_parent")
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public enum OperateType{
		index,create,update,delete;
	}
}
