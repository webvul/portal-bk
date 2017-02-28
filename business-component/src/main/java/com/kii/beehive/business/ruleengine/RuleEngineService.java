package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;

import com.kii.beehive.business.ruleengine.entitys.EngineBusinessObj;
import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.extension.sdk.commons.HttpTool;

@Component
public class RuleEngineService {
	
	private static final String AUTHORIZATION = "Authorization";
	private Logger log= LoggerFactory.getLogger(RuleEngineService.class);
	
	@Autowired
	private HttpTool httpTool;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private EngineTriggerBuilder  engineBuilder;
	
	
	@Autowired
	private BeehiveConfigDao configDao;
	
	
	@Value("${ruleengine.service.url}")
	private String  serviceUrl;
	private ObjectMapper mapperForBatchUpload;
	
	private RequestBuilder   fillRequest(RequestBuilder builder ,String subUrl){
		
		 builder
				.setUri(serviceUrl+subUrl)
				.setCharset(Charsets.UTF_8)
				.addHeader("Content-Type","application/json");
		return builder;
	}
	
	private HttpEntity  generEntity(Object obj){
		
		try {
			String json = mapper.writeValueAsString(obj);
			
			return new StringEntity(json,Charsets.UTF_8);
			
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
		
	}
	
	private Map<String,Object> getResult(String body){
		
		if(StringUtils.isBlank(body)){
			return Collections.emptyMap();
		}
		try {
			return  mapper.readValue(body,Map.class);
			
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
	
	private String doResponse(RequestBuilder builder ){
		try {
			
			HttpResponse response = httpTool.doRequest(builder.build());
			
			int statusCode=response.getStatusLine().getStatusCode();
		
			if(response.getStatusLine().getStatusCode()>=400){
				
				String body = StreamUtils.copyToString(response.getEntity().getContent(), Charsets.UTF_8);
				Map<String,Object> map=new HashMap();
				if(StringUtils.isNotBlank(body)) {
					map.putAll(mapper.readValue(body, Map.class));
				}
				throw new TriggerOperateException(map,statusCode);
				
			}
			HttpEntity entity=response.getEntity();
			if(entity!=null) {
				String body = StreamUtils.copyToString(entity.getContent(), Charsets.UTF_8);
				return body;
			}else{
				return null;
			}
			
		}catch (IllegalStateException ex) {
			
			log.error(ex.getMessage());
			throw new TriggerServiceException(ex);
			
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public String refreshAuthToken(String sysToken) {
		
		RequestBuilder builder = fillRequest(RequestBuilder.put(), "/sys/registGroup/" + engineBuilder.getGroupName());
		
		builder.setHeader("Authorization", sysToken);
		
		Map<String, Object> map = getResult(doResponse(builder));
		
		return (String) map.get("authToken");
	}
	
	public void setSecurityKey(String key, String sysToken) {
		
		RequestBuilder builder = fillRequest(RequestBuilder.put(), "/sys/registSecurityKey/" + engineBuilder.getGroupName());
		
		builder.setHeader(AUTHORIZATION, sysToken);
		builder.setEntity(generEntity(Collections.singletonMap("securityKey", key)));
		doResponse(builder);
		
	}
	
	public String  addTrigger(EngineTrigger trigger){
		RequestBuilder builder=fillRequest(RequestBuilder.post(),"/triggers/createTrigger");
		
		builder.setEntity(generEntity(trigger));

		Map<String,Object> map= getResult(doResponse(builder));
		
		return (String) map.get("triggerID");
	}
	
	public void updateTrigger(EngineTrigger trigger,String triggerID){
		
		RequestBuilder builder=fillRequest(RequestBuilder.put(),"/triggers/"+triggerID);
		
		builder.setEntity(generEntity(trigger));
		
		doResponse(builder);
	}
	
	public void removeTrigger(String triggerID){
		RequestBuilder builder=fillRequest(RequestBuilder.delete(),"/triggers/"+triggerID);
		
		doResponse(builder);
	}
	
	public void enableTrigger(String triggerID){
		RequestBuilder builder=fillRequest(RequestBuilder.put(),"/triggers/"+triggerID+"/recordStatus");
		
		Map<String,String> map=Collections.singletonMap("value","enable");
		builder.setEntity(generEntity(map));
		
		doResponse(builder);
	}

	public void disableTrigger(String triggerID){
		RequestBuilder builder=fillRequest(RequestBuilder.put(),"/triggers/"+triggerID+"/recordStatus");
		
		Map<String,String> map=Collections.singletonMap("value","disable");
		builder.setEntity(generEntity(map));
		
		doResponse(builder);
	}
	
	@PostConstruct
	public void init() {
		
		mapperForBatchUpload = new ObjectMapper();
		mapperForBatchUpload.configure(SerializationFeature.INDENT_OUTPUT, false);
	}
	
	public void updateBusinessData(Set<EngineBusinessObj> dataList, String authToken) {
		
		if (dataList == null || dataList.isEmpty()) {
			return;
		}
		String url="/groups/"+engineBuilder.getGroupName()+"/data/batchUpload";
		
		RequestBuilder builder=fillRequest(RequestBuilder.post(),url);
		
		StringBuilder sb=new StringBuilder();
		for(EngineBusinessObj obj:dataList){
			try {
				String json = mapperForBatchUpload.writeValueAsString(obj);
				sb.append(json).append("\n");
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
				continue;
			}
		}
		
		builder.setEntity(new StringEntity(sb.toString(),Charsets.UTF_8));
		
		builder.setHeader(AUTHORIZATION, authToken);
		
		String response=doResponse(builder);
		
		try {
			Set<String>  result = mapper.readValue(response,Set.class);
			
			if(!result.isEmpty()) {
				log.error("result:" + result);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
	}
}
