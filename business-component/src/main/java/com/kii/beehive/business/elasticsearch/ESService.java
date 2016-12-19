package com.kii.beehive.business.elasticsearch;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

@Component
public class ESService {
	

	
	private Logger log= LoggerFactory.getLogger(ESService.class);
	
	@Value("${elasticsearch.cluster.name}")
	private String clusterName;
	
	@Value("${elasticsearch.transport.address}")
	private String address;
	
	@Value("${elasticsearch.transport.port}")
	private int port;
	
	private RestClient client;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private ResourceLoader loader;
	
	private Queue<BulkOperate> queue=new ConcurrentLinkedQueue<>();
	
	@PostConstruct
	public void init(){
		
		client = RestClient.builder(
				new HttpHost(address, port, "http")).build();
	}
	
	@PreDestroy
	public void afterClose(){
		
		try {
			client.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
	}
	
	public void putDataMap(ESIndex index) throws IOException {
		
		
		
		InputStream stream=loader.getResource("classpath:com/kii/beehive/business/elasticsearch/"+index.getMapperTemplateName()).getInputStream();
		
		String json= StreamUtils.copyToString(stream, Charsets.UTF_8);
		
		ESRequest request=new ESRequest();
		request.setContent(json);
		request.setMethod(ESRequest.MethodType.PUT);
		request.setUrl("/"+index.getIndex().name());
		
		executeCall(request);
	}
	
	public String getDataMap(ESIndex index) throws IOException {
		
		
		InputStream stream=loader.getResource("classpath:com/kii/beehive/business/elasticsearch/"+index.getMapperTemplateName()).getInputStream();
		
		String json= StreamUtils.copyToString(stream, Charsets.UTF_8);
		
		ESRequest request=new ESRequest();
		request.setMethod(ESRequest.MethodType.GET);
		request.setUrl("/"+index.getIndex().name());
		
		String response=executeCall(request);
		
		return response;
	}
	
	public void addData(BulkOperate operate){
		
		queue.add(operate);
	}
	
	@Scheduled(fixedRate=60*1000*5,initialDelay=60*1000)
	public void doUpload(){
		
		StringBuilder sb=new StringBuilder();
		
//		try {
				
		BulkOperate entry=null;
		int idx=100;
		while( (entry=queue.poll())!=null) {
			
			Map<String,Object> map=new HashMap<>();
			map.put(entry.getOperate().name(),entry);
			
			try {
				String operLine = mapper.writeValueAsString(entry);
				String data = mapper.writeValueAsString(entry.getData());
				
				sb.append(operLine).append("\n");
				sb.append(data).append("\n");
			}catch(JsonProcessingException e){
				log.error(e.getMessage());
			}
			
			idx--;
			if(idx<0){
				break;
			}
		}

		sb.append("\n");
		
		ESRequest request=new ESRequest();
		request.setUrl("/_bulk");
		request.setContent(sb.toString());
		request.setMethod(ESRequest.MethodType.POST);
		
		executeRun(request);
	}
	
	
	
	private void executeRun(ESRequest  request){
		
		if(request.isLastTry()){
			return;
		}
		
		Map<String,String> paramMap=new HashMap<>();
		paramMap.put("Content-Type","application/json");
		
		client.performRequestAsync(request.getMethod().name(),request.getUrl(), paramMap,
				request.getRequestEntry(),
				new ResponseListener(){
					
					@Override
					public void onSuccess(Response response) {
						
						log.info(response.toString());
					}
					
					@Override
					public void onFailure(Exception exception) {
						log.error(exception.getMessage());
						
						try {
							Thread.sleep(1000*60);
							request.sub();
							executeCall(request);
							
						} catch (InterruptedException e) {
							log.error(e.getMessage());
						}
					}
				} );
	}
	
	private String executeCall(ESRequest  request){
		
		if(request.isLastTry()){
			return  null;
		}
		
		Map<String,String> paramMap=new HashMap<>();
		paramMap.put("Content-Type","application/json");
//		paramMap.put("","");
		
		try {
			Response response=client.performRequest(request.getMethod().name(),request.getUrl(), paramMap,
					request.getRequestEntry());
			
			HttpEntity  entity=response.getEntity();
			
			return StreamUtils.copyToString(entity.getContent(),Charsets.UTF_8);
			
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
	



}
