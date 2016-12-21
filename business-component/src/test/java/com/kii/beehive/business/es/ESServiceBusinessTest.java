package com.kii.beehive.business.es;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.BusinessTestTemplate;
import com.kii.beehive.business.elasticsearch.ESService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ESThingStatusService;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class ESServiceBusinessTest extends BusinessTestTemplate {

	@Autowired
	private ESThingStatusService  service;
	
	@Autowired
	private ESService es;
	
	@Autowired
	private ThingTagManager manager;
	
	@Test
	public void addThing() throws IOException {

//		es.putDataMap(ESIndex.thingInfo);
		
		service.updateThingEntitys();
		
		for (int i = 0; i < 2; i++) {
			es.doUpload();
		}
		
		System.in.read();
	}
	
	@Test
	public void updateStatus() throws IOException {

//		es.putDataMap(ESIndex.thingInfo);
		
		manager.getAllThingFullInfo().forEach(info->{

			int count=RandomUtils.nextInt(30,50);
			Calendar cal= Calendar.getInstance();
			
			for(int i=0;i<count;i++) {
				ThingStatus status = new ThingStatus();
				
				status.setField("HUM", RandomUtils.nextFloat(20, 100));
				status.setField("PM25", RandomUtils.nextInt(0, 50));
				status.setField("CO2", RandomUtils.nextInt(200, 1000));
				status.setField("CO", RandomUtils.nextFloat(0.001f, 0.01f));
				status.setField("TEP", RandomUtils.nextFloat(0, 40));
				status.setField("HCHO", RandomUtils.nextFloat(0.001f, 0.01f));
				
				cal.add(Calendar.DAY_OF_YEAR,-(RandomUtils.nextInt(3,7)));
				
				status.setField("date",cal.getTimeInMillis());
				
				service.addThingStatus(status, info.getKiicloudThingID());
			}
			es.doUpload();
		});
		System.in.read();
		
		
		
		
	}
	
	
	 
}
