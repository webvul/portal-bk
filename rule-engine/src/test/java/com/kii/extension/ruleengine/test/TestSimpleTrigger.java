package com.kii.extension.ruleengine.test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestSimpleTrigger extends InitTest {



	@Before
	public void init() throws IOException {



//		ruleLoader.initCondition(
//				getDrlContent("triggerComm")
//		);

		initGlobal();

	}





	@Test
	public void testTrigger100(){

		ruleLoader.addCondition("trigger100",getDrlContent("rule100"));
		String id = "100";

		Trigger trigger=new Trigger(id);

		//trigger.addThing(String.valueOf(0));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);

//		trigger.setTriggerID(id);

		ruleLoader.addOrUpdateData(trigger,true);

		addThing(id,String.valueOf(0));

		updateThingState("0", paramNo);
//		ruleLoader.fireCondition();

		updateThingState("0", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(id));

		updateThingState("0", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(id));


		updateThingState("0", paramNo);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(id));


		updateThingState("0", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(id));

	}



	@Test
	public void testTrigger101(){

		ruleLoader.addCondition("trigger",getDrlContent("rule101"));

		String triggerID = "101";

		Trigger trigger=new Trigger(triggerID);

		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_TRUE);


//		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger,true);

		String thingID = "1";

		addThing(triggerID,thingID);

		updateThingState(thingID);

//		ruleLoader.fireCondition();

		updateThingState(thingID,paramOk);
//		ruleLoader.fireCondition();
		assertTrue(isMatch(triggerID));

//		paramOk.put("foo",101);
		updateThingState(thingID,paramOk);
//		ruleLoader.fireCondition();
		assertTrue(isMatch(triggerID));


		updateThingState(thingID, paramNo);
//		ruleLoader.fireCondition();
		assertFalse(isMatch(triggerID));


		updateThingState(thingID,paramOk);
//		ruleLoader.fireCondition();
		assertTrue(isMatch(triggerID));

	}

	@Test
	public void testTrigger102(){

		ruleLoader.addCondition("trigger",getDrlContent("rule102"));

		String triggerID="102";

		Trigger trigger=new Trigger(triggerID);
		addThing(triggerID,"2");


		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_TRUE_TO_FALSE);

//		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger,true);

		updateThingState("2");

//		ruleLoader.fireCondition();

		updateThingState("2", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(0,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(triggerID));

		updateThingState("2", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

//		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(triggerID));

	}


	@Test
	public void testTrigger103(){

		ruleLoader.addCondition("trigger",getDrlContent("rule103"));

		String triggerID="103";

		Trigger trigger=new Trigger(triggerID);
		addThing(triggerID,"2");


		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_CHANGED);

//		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger,true);

		updateThingState("2");

//		ruleLoader.fireCondition();

		updateThingState("2", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

//		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(triggerID));

		updateThingState("2", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(3,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

//		ruleLoader.fireCondition();

		assertEquals(4,exec.getHitCount(triggerID));

		updateThingState("2", paramNo);

//		ruleLoader.fireCondition();

		assertEquals(4,exec.getHitCount(triggerID));

		updateThingState("2", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(5,exec.getHitCount(triggerID));

		updateThingState("2", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(5,exec.getHitCount(triggerID));




	}
}
