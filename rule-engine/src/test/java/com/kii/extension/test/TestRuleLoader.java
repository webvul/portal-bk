package com.kii.extension.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.store.trigger.TriggerWhen;
import com.kii.extension.drools.entity.Trigger;
import com.kii.extension.drools.entity.TriggerType;

public class TestRuleLoader extends InitTest {

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
//		trigger.setPreviousResult(false);

		String i = "100";
		trigger.setTriggerID(i);

		ruleLoader.addOrUpdateData(trigger);


		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(i));

		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(1,exec.getHitCount(i));

		updateThingState("0", paramNo);
		ruleLoader.fireCondition();


		updateThingState("0",paramOk);

		ruleLoader.addCondition("trigger101",getDrlContent("rule101"));

		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(i));

		updateThingState("0", paramOk);

		ruleLoader.fireCondition();

		assertEquals(2,exec.getHitCount(i));

	}

}
