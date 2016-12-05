package com.kii.extension.ruleengine.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestRuleLoader extends InitTest {

	@Before
	public void init() throws IOException {


//
//		ruleLoader.initCondition(
//				getDrlContent("triggerComm")
//		);

		initGlobal();


	}


	@Test
	public void testTrigger100(){

		ruleLoader.addCondition("trigger100",getDrlContent("rule100"));
		String i = "100";

		Trigger trigger=new Trigger(i);

		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);
//		trigger.setPreviousResult(false);

//		trigger.setTriggerID(i);

		ruleLoader.addOrUpdateData(trigger,true);

		addThing(i,String.valueOf(0));

		updateThingState("0", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(i));

		updateThingState("0", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(i));

		updateThingState("0", paramNo);
//		ruleLoader.fireCondition();


		updateThingState("0",paramOk);

		ruleLoader.addCondition("trigger101",getDrlContent("rule101"));

//		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(i));

		updateThingState("0", paramOk);

//		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(i));

	}

}
