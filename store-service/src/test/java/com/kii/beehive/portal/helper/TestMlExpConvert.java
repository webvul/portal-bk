package com.kii.beehive.portal.helper;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.StoreServiceTestInit;
import com.kii.beehive.business.ruleengine.TriggerConvertTool;

public class TestMlExpConvert extends StoreServiceTestInit {
	
	
	
	@Autowired
	private TriggerConvertTool convertTool;
	
	
	@Test
	public void testFillExpress(){
		
		String input="ml.score('one',$p{1},$p{2})>$p{demo.map[c].num} ";
		
		String result=convertTool.addParamPrefix(input,"ml_output");
		
		assertEquals("ml.score('one',$p{ml_output.1},$p{ml_output.2})>$p{ml_output.demo.map[c].num} ",result);
	}
	
}
