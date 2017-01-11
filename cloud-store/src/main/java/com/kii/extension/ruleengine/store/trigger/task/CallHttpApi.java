package com.kii.extension.ruleengine.store.trigger.task;

import java.util.HashMap;
import java.util.Map;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;

public class CallHttpApi extends ExecuteTarget {

	@Override
	public TargetType getType() {
		return TargetType.HttpApiCall;
	}



	public void fillParam(Map<String, String> params) {

		url= StrTemplate.generByMap(url,params);

		content=StrTemplate.generByMap(content,params);
		authorization=StrTemplate.generByMap(authorization,params);
		contentType=StrTemplate.generByMap(contentType,params);
	}

	private String url;

	private Map<String,String> headers=new HashMap<>();

	private String content="{}";

	private String authorization;

	private String contentType;

	private HttpMethod  method=HttpMethod.POST;



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void addHeader(String name,String value){
		this.headers.put(name,value);
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}



	public enum HttpMethod{
		PUT,POST,GET,DELETE,PATCH;
	}
}
