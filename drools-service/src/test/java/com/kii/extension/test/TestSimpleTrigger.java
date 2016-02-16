package com.kii.extension.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;

public class TestSimpleTrigger extends InitTest {



	@Before
	public void init() throws IOException {


		ruleLoader.initCondition(
				getDrlContent("triggerComm")
		);

//
//		for(int i=0;i<10;i++){
//			updateThingState(String.valueOf(i));
//		}

	}


	@Test
	public void testTrigger100(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(0));

		trigger.setType("simple");
		trigger.setWhen("false2true");
		trigger.setPreviousResult(false);

		trigger.setTriggerID(100);

		ruleLoader.addOrUpdateData(trigger);
//
//		updateThingState("0");
//
//		ruleLoader.fireCondition();

		Map<String,Object> param=new HashMap<>();
		param.put("foo",100);
		param.put("bar",-10);


		Map<String,Object> param1=new HashMap<>();
		param1.put("foo",-100);
		param1.put("bar",10);


		updateThingState("0",param);

		ruleLoader.fireCondition();

		updateThingState("0",param);

		ruleLoader.fireCondition();

		updateThingState("0",param1);

		ruleLoader.fireCondition();

		updateThingState("0",param);

		ruleLoader.fireCondition();
	}

	@Test
	public void testTrigger101(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(1));

		trigger.setType("simple");
		trigger.setWhen("true");

		trigger.setTriggerID(101);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("1");

		ruleLoader.fireCondition();

		Map<String,Object> param=new HashMap<>();
		param.put("foo",100);
		param.put("bar",-10);

		updateThingState("1",param);

		ruleLoader.fireCondition();


		updateThingState("1",param);

		ruleLoader.fireCondition();

		updateThingState("1",param);

		ruleLoader.fireCondition();


		updateThingState("1",param);

		ruleLoader.fireCondition();
	}

	@Test
	public void testTrigger102(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(2));

		trigger.setType("simple");
		trigger.setWhen("true2false");

		trigger.setTriggerID(102);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("2");

		ruleLoader.fireCondition();

		Map<String,Object> param=new HashMap<>();
		param.put("foo",100);
		param.put("bar",-10);

		updateThingState("2",param);

		ruleLoader.fireCondition();


		param.put("foo",-100);
		param.put("bar",100);
		updateThingState("2",param);

		ruleLoader.fireCondition();

		param.put("foo",100);
		param.put("bar",-100);
		updateThingState("2",param);

		ruleLoader.fireCondition();

		param.put("foo",-100);
		param.put("bar",100);
		updateThingState("2",param);

		ruleLoader.fireCondition();
	}
}
