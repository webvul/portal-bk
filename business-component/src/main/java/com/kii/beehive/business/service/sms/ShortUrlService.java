package com.kii.beehive.business.service.sms;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.extension.sdk.commons.HttpTool;

@Component
public class ShortUrlService {

	@Autowired
	private HttpTool httpTool;

	@Autowired
	private ObjectMapper  mapper;



	public String getShortUrl(String fullUrl){

		try {
			List<NameValuePair> params = new ArrayList<>();
			NameValuePair pair = new BasicNameValuePair("url", fullUrl);
			params.add(pair);

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
			HttpPost request = new HttpPost("http://www.dwz.cn/create.php");
			request.setEntity(entity);


			HttpResponse response=httpTool.doRequest(request);

			String json = StreamUtils.copyToString(response.getEntity().getContent(), Charsets.UTF_8);

			Map<String,Object> map=mapper.readValue(json,Map.class);

			int status= (int) map.get("status");

			if(status!=0){
				throw new IllegalArgumentException("get tinyurl fail:"+map.get("err_msg"));
			}

			String tinyUrl= (String) map.get("tinyurl");

			return tinyUrl;

		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}



	}

	/*
	{"tinyurl":"http:\/\/dwz.cn\/3JcNce","status":0,"longurl":"https://docs.google.com/document/d/1-3n2yKq_RQurI-rlcZJ3xpbCQLnoaVgTGAjihyIW6HM/edit#heading=h.3dy6vkm","err_msg":""}
	 */


}
