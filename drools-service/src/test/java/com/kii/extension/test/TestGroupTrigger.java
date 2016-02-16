package com.kii.extension.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;

public class TestGroupTrigger extends InitTest {


	@Before
	public void init() throws IOException {


		ruleLoader.initCondition(
				getDrlContent("triggerComm"),
				getDrlContent("groupPolicy")
		);

//
//		for(int i=0;i<10;i++){
//			updateThingState(String.valueOf(i));
//		}

	}

	@Test
	public void testTrigger200(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();

		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("all");
		trigger.setWhen("false2true");
		trigger.setPreviousResult(false);

		trigger.setTriggerID(200);

		ruleLoader.addOrUpdateData(trigger);

		Map<String,Object> paramOk=new HashMap<>();
		paramOk.put("foo",100);
		paramOk.put("bar",-10);


		Map<String,Object> paramNo=new HashMap<>();
		paramNo.put("foo",-100);
		paramNo.put("bar",10);

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
			ruleLoader.fireCondition();

		}

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
			ruleLoader.fireCondition();

		}

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
			ruleLoader.fireCondition();

		}

	}

	@Test
	public void testTrigger201(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();

		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("any");
		trigger.setWhen("false2true");
		trigger.setPreviousResult(false);

		trigger.setTriggerID(201);

		ruleLoader.addOrUpdateData(trigger);

		Map<String,Object> paramOk=new HashMap<>();
		paramOk.put("foo",100);
		paramOk.put("bar",-10);


		Map<String,Object> paramNo=new HashMap<>();
		paramNo.put("foo",-100);
		paramNo.put("bar",10);

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
			ruleLoader.fireCondition();

		}

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
			ruleLoader.fireCondition();

		}

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramNo);
			ruleLoader.fireCondition();
		}

		updateThingState("1",paramOk);
		ruleLoader.fireCondition();

	}


	@Test
	public void testTrigger202(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();

		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("number");
		trigger.setNumber(3);
		trigger.setWhen("false2true");
		trigger.setPreviousResult(false);

		trigger.setTriggerID(202);

		ruleLoader.addOrUpdateData(trigger);

		Map<String,Object> paramOk=new HashMap<>();
		paramOk.put("foo",100);
		paramOk.put("bar",-10);


		Map<String,Object> paramNo=new HashMap<>();
		paramNo.put("foo",-100);
		paramNo.put("bar",10);

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2!=0?paramOk:paramNo);
		}
		ruleLoader.fireCondition();

//
		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
		}
		ruleLoader.fireCondition();

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramNo);
//			ruleLoader.fireCondition();

		}
		ruleLoader.fireCondition();
//
//		for(int i=0;i<5;i++){
//			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
//			ruleLoader.fireCondition();
//
//		}

	}

}
