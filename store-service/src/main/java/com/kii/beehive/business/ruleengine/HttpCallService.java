package com.kii.beehive.business.ruleengine;


import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;

import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.service.ExecuteResultDao;
import com.kii.extension.ruleengine.store.trigger.target.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.target.HttpCallResponse;
import com.kii.extension.sdk.commons.HttpTool;

@Component
public class HttpCallService {

	@Autowired
	private HttpTool tool;

	private Logger log= LoggerFactory.getLogger(HttpCallService.class);

	@Autowired
	private ExecuteResultDao  resultDao;


	@Autowired
	private ResponseBuilder  builder;

	public void doHttpApiCall(CallHttpApi call,String triggerID ,ExecuteParam params){


		HttpCallResponse result=builder.getHttpResponse(triggerID,params);

		try {

			call.fillParam(params.getBusinessParams());

			HttpResponse response = tool.doRequest(getRequest(call));


			int code=response.getStatusLine().getStatusCode();

			log.info("http call target:"+code);

			result.setHttpRequest(call);

			result.setStatus(response.getStatusLine().getStatusCode());

			HttpEntity  entity=response.getEntity();
			if(entity!=null&&entity.isStreaming()){

				Header hEncode=entity.getContentEncoding();

				String encode="UTF-8";
				if(hEncode!=null) {
					 encode = hEncode.getValue();
				}
				for(Header head:response.getAllHeaders()){
					result.addHeader(head.getName(),head.getValue());
				}
				try {

					String ctx= StreamUtils.copyToString(entity.getContent(), Charset.forName(encode));
					result.setBody(ctx);

				} catch (IOException e) {
					log.warn("get response body fail",e);
				}
			}

		}catch(Exception ex){

			result.bindException(ex);
		}

		resultDao.addResponse(result);



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
