package com.kii.extension.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.StatelessRuleExecute;
import com.kii.extension.ruleengine.drools.DroolsRuleService;
import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.ThingStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:./ruleTestContext.xml"})
public class InitTest {


	@Autowired
	protected DroolsRuleService ruleLoader;


	@Autowired
	protected StatelessRuleExecute execute;


	@Autowired
	protected ResourceLoader loader;


	protected String getDrlContent(String fileName) {

		try {
			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}




	protected  void updateThingState(String thingID,Map<String,Object> values){
		ThingStatus status=new ThingStatus();
		status.setThingID(thingID);

		status.setValues(values);

		addThingStatus(thingID, status);
	}

	private void addThingStatus(String thingID, ThingStatus status) {


		ruleLoader.addOrUpdateData(status);

		CurrThing curr=new CurrThing();
		curr.setThing(thingID);

		ruleLoader.setGlobal("currThing",curr);


	}

	protected void updateThingState(String thingID){

		ThingStatus status=new ThingStatus();
		status.setThingID(thingID);

		status.addValue("foo",Math.random()*100-50);
		status.addValue("bar",Math.random()*10-5);

		addThingStatus(thingID, status);

	}
}
