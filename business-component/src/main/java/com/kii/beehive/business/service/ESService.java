package com.kii.beehive.business.service;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.StringEntity;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
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
	
	private Map<ESIndex,Queue> queueMap=new  HashMap<>();
	
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
	
	public void setDataMap(ESIndex index){
		
		
		InputStream stream=loader.getResource("classpath:com/kii/beehive/business/elasticsearch/"+index.getMapperTemplateName()).getInputStream();
		
		String json= StreamUtils.copyToString(stream, Charsets.UTF_8);
		
		
	}
	
	public void addData(ESIndex index,Object data){
		
		Queue queue=queueMap.putIfAbsent(index,new ConcurrentLinkedQueue());
		
		queue.add(data);
	}
	
	@Scheduled(fixedRate=60*1000*5,initialDelay=60*1000)
	public void doUpload(){
		
		StringBuilder sb=new StringBuilder();
		
		queueMap.forEach((k,v)->{
			
			Map<String,Object> operate= Collections.singletonMap("create",k);
			try {
				String operLine = mapper.writeValueAsString(operate);
				
				Object entry=v.poll();
				while(entry!=null) {
					String data = mapper.writeValueAsString(entry);
					
					sb.append(operLine).append("\n");
					sb.append(data).append("\n");
					
					entry=v.poll();
				}
				
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
			}
		};
		
		sb.append("\n");
		
		HttpEntity entity=new StringEntity(sb.toString(), Charsets.UTF_8);
		
		executeCall(entity,0);
	}
	
	private void executeCall(HttpEntity entity,final int retry){
		
		if(retry>3){
			return;
		}
		
		Map<String,String> paramMap=new HashMap<>();
		paramMap.put("Content-Type","application/json");
		
		client.performRequestAsync("POST", "/_bulk", paramMap,
				entity,
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
							executeCall(entity,retry+1);
							
						} catch (InterruptedException e) {
							log.error(e.getMessage());
						}
					}
				} );
	}
	
	
	public static class ESIndex {
		
		private final IndexEnum index;
		
		private final TypeEnum type;
		
		
		public ESIndex(IndexEnum index) {
			this.index = index;
			this.type = null;
		}
		
		public ESIndex(IndexEnum index, TypeEnum type) {
			this.index = index;
			this.type = type;
		}
		
		
		@JsonProperty("_index")
		public IndexEnum getIndex() {
			return index;
		}
		
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ESIndex esIndex = (ESIndex) o;
			return Objects.equals(index, esIndex.index) &&
					Objects.equals(type, esIndex.type);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(index, type);
		}
		
		@JsonProperty("_type")
		public TypeEnum getType() {
			return type;
		}
		
		@JsonIgnore
		public String getMapperTemplateName(){
			if(type==null){
				return index.name()+".mapper.json";
			}else{
				return index.name()+"."+type.name()+".mapper.json";
			}
		}
	}
	
	public enum IndexEnum{
		thingstatus;
	}
	
	public enum TypeEnum{
		
	}

	
	public static class ESMapperEntry{
		
		private final String value;
		
		public ESMapperEntry(String value){
			this.value=value;
		}
		
		@JsonRawValue
		public String getValue() {
			return value;
		}
	
	}
}
