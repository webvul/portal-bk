package com.kii.extension.ruleengine.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.TriggerWhen;

public class TestScheduleTrigger extends InitTest {

	@Before
	public void init() throws IOException {



		ruleLoader.initCondition(
				getDrlContent("triggerComm")
		);

		initGlobal();

	}


	@Test
	public void testCron() throws IOException {

		ruleLoader.addCondition("schedule",getDrlContent("ruleWithSchedule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(10));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(TriggerWhen.CONDITION_FALSE_TO_TRUE);

		String id = "500";
		trigger.setTriggerID(id);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("10", paramOk);

//		ruleLoader.fireCondition();

		System.in.read();


	}

	@Test
	public void testInterval() throws IOException, InterruptedException {

		ruleLoader.addCondition("schedule",getDrlContent("ruleWithSchedule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(11));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(TriggerWhen.CONDITION_FALSE_TO_TRUE);

		String id = "501";
		trigger.setTriggerID(id);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("11", paramOk);

//		ruleLoader.fireCondition();

		Thread.sleep(60*1000*2);

		updateThingState("11",paramNo);

		System.in.read();

	}

	@Test
	public void testPeriod() throws IOException, InterruptedException {

		ruleLoader.addCondition("schedule",getDrlContent("ruleWithSchedule"));

		Trigger trigger=new Trigger();
		trigger.addThing(String.valueOf(11));

		trigger.setType(TriggerType.simple);
		trigger.setWhen(TriggerWhen.CONDITION_FALSE_TO_TRUE);

		String id = "502";
		trigger.setTriggerID(id);

		ruleLoader.addOrUpdateData(trigger);

		updateThingState("11", paramOk);

		ruleLoader.fireCondition();


	}
}
