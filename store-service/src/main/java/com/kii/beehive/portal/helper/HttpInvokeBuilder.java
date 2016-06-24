package com.kii.beehive.portal.helper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.Charsets;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpInvokeBuilder {

	private String token;

	private Map<String, String> optionalHeader = new HashMap<>();

	private Object ctxObj = null;
	private String fileName;

	private HttpUriRequest request;

	private String url;

	public HttpInvokeBuilder() {
		setContentType("application/json");
	}

	public HttpInvokeBuilder setContentType(String value) {
		optionalHeader.put("Content-Type", value);
		return this;
	}
	public HttpInvokeBuilder clearContentType() {
		optionalHeader.clear();
		return this;
	}

	public void setConsumeHeader(String name, String value) {
		optionalHeader.put(name, value);
	}

	public HttpInvokeBuilder setUrl(String url) {

		this.url = url;

		return this;
	}

	public HttpInvokeBuilder setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public HttpInvokeBuilder buildCustomCall(String type, Object obj) {

		this.ctxObj = obj;
		switch (type.toLowerCase()) {
			case "post":
				request = new HttpPost(url);
				break;
			case "get":
				request = new HttpGet(url);
				break;
			case "put":
				request = new HttpPut(url);
				break;
			case "delete":
				request = new HttpDelete(url);
				break;

		}


		return this;
	}


	//==============================
	//
	//==============================

	public HttpUriRequest generRequest(ObjectMapper mapper) {


		for (Map.Entry<String, String> entry : optionalHeader.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}

		if (request instanceof HttpEntityEnclosingRequestBase && ctxObj != null) {

			if (ctxObj instanceof String) {
				((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity((String) ctxObj, Charsets.UTF_8));
			} else if (ctxObj instanceof File) {

				// 把文件转换成流对象FileBody
				File file =  (File) ctxObj;
				MultipartEntity mpEntity = new MultipartEntity();
//				ContentBody cbFile = new FileBody(file, "image/jpeg");
				ContentBody cbFile = new FileBody(file);
				mpEntity.addPart(this.fileName, cbFile);

				((HttpPost) request).setEntity(mpEntity);

			} else {
				try {
					String context = mapper.writeValueAsString(ctxObj);
					((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(context, Charsets.UTF_8));

				} catch (JsonProcessingException e) {
					throw new IllegalArgumentException(e);
				}

			}
		}


		ctxObj = null;
		optionalHeader.clear();

		return request;
	}


}
