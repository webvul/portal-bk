package com.kii.extension.test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.MemberCountResult;
import com.kii.extension.ruleengine.drools.entity.Trigger;

public class TestGroupTrigger extends InitTest {


	@Before
	public void init() throws IOException {


		ruleLoader.initCondition(
				getDrlContent("triggerComm"),
				getDrlContent("groupPolicy")
		);

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		initGlobal();

	}

	@Test
	public void testTrigger200(){


		Trigger trigger=new Trigger();

		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("all");
		trigger.setWhen("false2true");
//		trigger.setPreviousResult(false);

		String triggerID = "200";
		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);



		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
			ruleLoader.fireCondition();
		}

		assertEquals(0,exec.getHitCount(triggerID));


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
			ruleLoader.fireCondition();
		}

		assertEquals(1,exec.getHitCount(triggerID));


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2!=0?paramOk:paramNo);
			ruleLoader.fireCondition();

		}

		assertEquals(1,exec.getHitCount(triggerID));


	}


	@Test
	public void testTrigger201(){


		Trigger trigger=new Trigger();

		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("any");
		trigger.setWhen("true");
//		trigger.setPreviousResult(false);

		String triggerID = "201";
		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);

		MemberCountResult result=new MemberCountResult();
		result.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(result);


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
			ruleLoader.fireCondition();


			assertTrue(isMatch(triggerID));
		}

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
			ruleLoader.fireCondition();
			assertTrue(isMatch(triggerID));
		}

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramNo);
			ruleLoader.fireCondition();
		}
		assertFalse(isMatch(triggerID));


		updateThingState("1",paramOk);
		ruleLoader.fireCondition();

		assertTrue(isMatch(triggerID));


	}


	@Test
	public void testTrigger202(){


		Trigger trigger=new Trigger();

		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("number");
		trigger.setNumber(3);
		trigger.setWhen("false2true");
//		trigger.setPreviousResult(false);

		String triggerID = "202";
		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2!=0?paramOk:paramNo);
		}
		ruleLoader.fireCondition();
		assertEquals(0,exec.getHitCount(triggerID));

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
		}
		ruleLoader.fireCondition();
		assertEquals(1,exec.getHitCount(triggerID));


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramNo);

		}
		ruleLoader.fireCondition();
		assertEquals(1,exec.getHitCount(triggerID));


	}

}
