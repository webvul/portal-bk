package com.kii.extension.ruleengine.test;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;

public class TestExtend extends InitTest{

	private Logger log= LoggerFactory.getLogger(TestExtend.class);



	@Test
	public void testParam() throws IOException {


		ThingStatusInRule status=new ThingStatusInRule("aaa");
		status.addValue("foo",33);
		status.addValue("bar",-100);
		status.addValue("delay",2);

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
