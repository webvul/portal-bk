package com.kii.extension.ruleengine.test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.TriggerWhen;

public class TestSimpleTrigger extends InitTest {



	@Before
	public void init() throws IOException {



		ruleLoader.initCondition(
				getDrlContent("triggerComm")
		);

		initGlobal();

	}





	@Test
	public void testTrigger100(){

		ruleLoader.addCondition("trigger100",getDrlContent("rule100"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(0));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(TriggerWhen.CONDITION_FALSE_TO_TRUE);

		String id = "100";
		trigger.setTriggerID(id);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("0", paramNo);
		ruleLoader.fireCondition();

		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(id));

		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(id));


		updateThingState("0", paramNo);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(id));


		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(id));

	}

	@Test
	public void testTrigger101(){

		ruleLoader.addCondition("trigger",getDrlContent("rule101"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(1));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(TriggerWhen.CONDITION_TRUE);

		String triggerID = "101";

		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);

		String thingID = "1";

		updateThingState(thingID);

		ruleLoader.fireCondition();

		updateThingState(thingID,paramOk);
		ruleLoader.fireCondition();
		assertTrue(isMatch(triggerID));

//		paramOk.put("foo",101);
		updateThingState(thingID,paramOk);
		ruleLoader.fireCondition();
		assertTrue(isMatch(triggerID));


		updateThingState(thingID, paramNo);
		ruleLoader.fireCondition();
		assertFalse(isMatch(triggerID));


		updateThingState(thingID,paramOk);
		ruleLoader.fireCondition();
		assertTrue(isMatch(triggerID));

	}

	@Test
	public void testTrigger102(){

		ruleLoader.addCondition("trigger",getDrlContent("rule102"));

		String triggerID="102";

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(2));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(TriggerWhen.CONDITION_TRUE_TO_FALSE);

		trigger.setTriggerID(triggerID);

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
