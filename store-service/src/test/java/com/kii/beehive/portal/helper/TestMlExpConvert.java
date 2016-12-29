package com.kii.beehive.portal.helper;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.services.MLTriggerService;
import com.kii.beehive.portal.store.StoreServiceTestInit;

public class TestMlExpConvert extends StoreServiceTestInit {
	
	
	@Autowired
	private MLTriggerService service;
	
	@Test
	public void testFillExpress(){
		
		String input="ml.score('one',$p{1},$p{2})>$p{demo.map[c].num} ";
		
		String result=service.addParamPrefix(input);
		
		assertEquals("ml.score('one',$p{ml_output.1},$p{ml_output.2})>$p{ml_output.demo.map[c].num} ",result);
	}
	
}
