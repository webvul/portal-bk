package com.kii.extension.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;

public class TestSimpleTrigger extends InitTest {


	Map<String,Object> paramOk =new HashMap<>();

	Map<String,Object> paramNo =new HashMap<>();

	@Before
	public void init() throws IOException {



		ruleLoader.initCondition(
				getDrlContent("triggerComm")
		);

		initGlobal();

//
//		for(int i=0;i<10;i++){
//			updateThingState(String.valueOf(i));
//		}

		paramNo.put("foo",-100);
		paramNo.put("bar",10);

		paramOk.put("foo",100);
		paramOk.put("bar",-10);

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



		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(100));

		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(100));


		updateThingState("0", paramNo);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(100));


		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(100));

	}

	@Test
	public void testTrigger101(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(1));

		trigger.setType("simple");
		trigger.setWhen("true");

		int triggerID = 101;

		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);

		String thingID = "1";

		updateThingState(thingID);

		ruleLoader.fireCondition();

		updateThingState(thingID,paramOk);
		ruleLoader.fireCondition();
		assertEquals(1,exec.getHitCount(triggerID));

//		paramOk.put("foo",101);
		updateThingState(thingID,paramOk);
		ruleLoader.fireCondition();
		assertEquals(2,exec.getHitCount(triggerID));


		updateThingState(thingID, paramNo);
		ruleLoader.fireCondition();
		assertEquals(2,exec.getHitCount(triggerID));


		updateThingState(thingID,paramOk);
		ruleLoader.fireCondition();
		assertEquals(3,exec.getHitCount(triggerID));

	}

	@Test
	public void testTrigger102(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		int triggerID=102;

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(2));

		trigger.setType("simple");
		trigger.setWhen("true2false");

		trigger.setTriggerID(102);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("2");

		ruleLoader.fireCondition();

		updateThingState("2", paramOk);

		ruleLoader.fireCondition();

		assertEquals(0,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(triggerID));

		updateThingState("2", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(triggerID));

	}
}
