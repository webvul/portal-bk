package com.kii.beehive.business.ruleengine;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;

import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
import com.kii.extension.sdk.commons.HttpTool;

@Component
public class HttpCallService {

	@Autowired
	private HttpTool tool;


	public void doHttpApiCall(CallHttpApi call ){



		tool.doRequest(getRequest(call));

	}

	private HttpUriRequest getRequest(CallHttpApi call){

		String url=call.getUrl();

		int idx=url.indexOf("://");
		if(idx==-1){
			url="http://"+url;
		}

		RequestBuilder  builder= RequestBuilder.create(call.getMethod().name())
				.setUri(url);

		if(StringUtils.isEmpty(call.getContentType())) {
			builder.setHeader("Content-Type","html/text");
		}else{
			builder.setHeader("Content-Type",call.getContentType());
		}

		if(!StringUtils.isEmpty(call.getAuthorization())){
			builder.setHeader("Authorization",call.getAuthorization());
		}

		call.getHeaders().forEach((k,v)->{
			builder.setHeader(k,v);
		});

		HttpEntity entity=new StringEntity(call.getContent(), Charsets.UTF_8);

		builder.setEntity(entity);

		return builder.build();
	}


}
