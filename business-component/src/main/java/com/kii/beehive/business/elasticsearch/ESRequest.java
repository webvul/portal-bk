package com.kii.beehive.business.elasticsearch;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import com.google.common.base.Charsets;

public class ESRequest {
	
	private String url;
	
	private MethodType method;
	
	private String content;
	
	private int retry=3;
	
	public boolean isLastTry(){
		return retry<=0;
	}
	
	public void sub() {
		this.retry-=1;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public MethodType getMethod() {
		return method;
	}
	
	public void setMethod(MethodType method) {
		this.method = method;
	}
	
	public HttpEntity getRequestEntry() {
		HttpEntity entity=new StringEntity(content.toString(), Charsets.UTF_8);
		return entity;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public enum MethodType{
		POST,GET,PUT,DELETE;
	}
}
