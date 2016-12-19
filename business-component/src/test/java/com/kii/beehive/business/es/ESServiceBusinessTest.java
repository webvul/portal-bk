package com.kii.beehive.business.es;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.BusinessTestTemplate;
import com.kii.beehive.business.elasticsearch.ESIndex;
import com.kii.beehive.business.elasticsearch.ESService;
import com.kii.beehive.business.service.ESThingStatusService;

public class ESServiceBusinessTest extends BusinessTestTemplate {

	@Autowired
	private ESThingStatusService  service;
	
	@Autowired
	private ESService es;
	
	@Test
	public void addThing() throws IOException {
		
		es.putDataMap(ESIndex.thingInfo);
	
		service.updateThingEntitys();
		
		
	}
	
	
	 
}
