package com.kii.beehive.business.ruleEngine;


import static junit.framework.TestCase.fail;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.business.BusinessTestTemplate;
import com.kii.beehive.business.ruleengine.EngineTriggerBuilder;
import com.kii.beehive.business.ruleengine.TriggerException;
import com.kii.beehive.business.ruleengine.TriggerOperate;
import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;


public class TestTrigger extends BusinessTestTemplate {
	
	String[] array = {"simple",
			"simple2", "test1", "test2",
			"test3", "monitor", "muiTest",
			"summary3", "combine", "group1", "testLeon",
			"condition1", "condition2"};
	private Logger log= LoggerFactory.getLogger(TestTrigger.class);
	@Autowired
	private TriggerOperate operate;
	@Autowired
	private ResourceLoader loader;
	@Autowired
	private EngineTriggerBuilder builder;
	@Autowired
	private ObjectMapper mapper;

	private TriggerRecord getTriggerInst(String name) throws IOException {
		
		String json= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/business/ruleengine/"+name+".json").getInputStream(), Charsets.ISO_8859_1);
		
		
		TriggerRecord record=mapper.readValue(json,TriggerRecord.class);
		record.setUserID(0l);
		record.setName(name);
		
		return record;
		
	}
	
	@Test
	public void testMonitorTrigger() throws IOException {
		String name = "summary";
		TriggerRecord record = getTriggerInst(name);
		
		try {
			EngineTrigger trigger = builder.generEngineTrigger(record);
			
			String result = mapper.writeValueAsString(trigger);
			
			log.debug("result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(name + " " + e.getMessage());
		}
	}
	
	@Test
	public void convertTrigger() throws IOException {
		

		for(String name:array) {
			TriggerRecord record = getTriggerInst(name);
			
			try {
				builder.generEngineTrigger(record);
			}catch(Exception e){
				e.printStackTrace();
				log.error(name+" "+e.getMessage());
			}
		}
		
	}
	@Test
	public void testTriggerCreate() throws IOException {
		
		for(String name:array) {
			
			log.info("trigger "+name);
			try {
				TriggerRecord record = getTriggerInst(name);
				
				String id = operate.createTrigger(record);
				
				operate.disableTrigger(id);
				
				operate.enableTrigger(id);
				
				operate.updateTrigger(record);
				
				operate.removeTrigger(id);
			}catch(TriggerException e){
				log.error(e.getErrorCode()+e.getStatusCode()+" name:"+name);
				e.printStackTrace();
				fail();
			}catch (Exception e){
				log.error(e.getMessage()+" name:"+name);
				e.printStackTrace();
				fail();
			}
		}
		
	}
	
	
}
