package com.kii.beehive.portal.web;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest extends com.kii.beehive.WebTestTemplate {
	
	
	@BeforeClass
	public static void setSystemProps() {
		System.setProperty("spring.profile","test");
		
	}
	
	@Test
	public void testLogLoader(){
		
		Logger log= LoggerFactory.getLogger(LogTest.class);
		
		log.info(">>>>>>>>>>>>>>>");
	}
	
	
}
