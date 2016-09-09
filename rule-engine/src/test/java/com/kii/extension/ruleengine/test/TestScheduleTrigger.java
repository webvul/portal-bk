package com.kii.extension.ruleengine.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestScheduleTrigger extends InitTest {


	@Autowired
	private ScheduleService  scheduleService;

	@Before
	public void init() throws IOException {



//		ruleLoader.initCondition(
//				getDrlContent("triggerComm")
//		);

		initGlobal();

	}


	@Test
	public void testCron() throws IOException {

		ruleLoader.addCondition("schedule",getDrlContent("ruleWithSchedule"));
		String id = "500";

		Trigger trigger=new Trigger(id);

		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);
//
//		trigger.setTriggerID(id);

//		addThing(id,String.valueOf(10));


		ruleLoader.addOrUpdateData(trigger);

		updateThingState("10", paramOk);

//		ruleLoader.fireCondition();

		System.in.read();


	}

	@Test
	public void testInterval() throws IOException, InterruptedException {

		ruleLoader.addCondition("schedule",getDrlContent("ruleWithSchedule"));

		String id = "501";

		Trigger trigger=new Trigger(id);

		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);

//		trigger.setTriggerID(id);

		ruleLoader.addOrUpdateData(trigger);

		addThing(id,String.valueOf(11));


		updateThingState("11", paramOk);

//		ruleLoader.fireCondition();

		Thread.sleep(60*1000*2);

		updateThingState("11",paramNo);

		System.in.read();

	}

	@Test
	public void testPeriod() throws IOException, InterruptedException {

		ruleLoader.addCondition("schedule",getDrlContent("ruleWithSchedule"));
		String id = "502";

		Trigger trigger=new Trigger(id);

		trigger.setType(TriggerType.simple);
		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);

//		trigger.setTriggerID(id);

		addThing(id,String.valueOf(11));


		ruleLoader.addOrUpdateData(trigger);

		updateThingState("11", paramOk);

//		ruleLoader.fireCondition();


	}
}
