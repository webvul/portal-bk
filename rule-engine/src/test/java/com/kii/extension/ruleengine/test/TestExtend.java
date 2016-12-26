package com.kii.extension.ruleengine.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;

public class TestExtend extends InitTest{

	private Logger log= LoggerFactory.getLogger(TestExtend.class);



	@Test
	public void testParam() throws IOException {


		BusinessObjInRule status=new BusinessObjInRule("aaa");
		
		Map<String,Object> val=new HashMap<>();
		
		val.put("foo",33);
		val.put("bar",-100);
		val.put("delay",2);
		
		status.setValues(val);
		
		ruleLoader.addCondition("ruleResult",getDrlContent("ruleResult"));
//		ruleLoader.fireCondition();
		ruleLoader.addOrUpdateData(status,true);

		ExternalValues  ext=new ExternalValues("name");
		ext.addValue("abc",3);

		ruleLoader.addOrUpdateExternal(ext);

//		ruleLoader.fireCondition();

//		System.in.read();


	}
}
