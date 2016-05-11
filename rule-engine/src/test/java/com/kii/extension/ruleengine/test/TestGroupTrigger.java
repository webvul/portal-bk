package com.kii.extension.ruleengine.test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.ruleengine.drools.entity.Group;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicyType;
import com.kii.extension.ruleengine.store.trigger.WhenType;

//import com.kii.extension.ruleengine.drools.entity.MemberCountResult;

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



		String triggerID = "200";

		Group group=new Group();

		trigger.setType(TriggerType.group);
		group.setPolicy(TriggerGroupPolicyType.All);
		group.setTriggerID(triggerID);

		for(int i=0;i<5;i++) {
			group.addThing(String.valueOf(i));
		}

		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);
//		trigger.setPreviousResult(false);


		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);
		ruleLoader.addOrUpdateData(group);



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

		String triggerID = "201";

		Group group=new Group();
		group.setTriggerID(triggerID);
		for(int i=0;i<5;i++) {
			group.addThing(String.valueOf(i));
		}
		group.setPolicy(TriggerGroupPolicyType.Any);
		trigger.setType(TriggerType.group);

		trigger.setWhen(WhenType.CONDITION_TRUE);
//		trigger.setPreviousResult(false);

		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);

		ruleLoader.addOrUpdateData(group);

//		MemberCountResult result=new MemberCountResult();
//		result.setTriggerID(triggerID);
//
//		ruleLoader.addOrUpdateData(result);


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
		String triggerID = "202";

		Group group=new Group();
		group.setTriggerID(triggerID);

		for(int i=0;i<5;i++) {
			group.addThing(String.valueOf(i));
		}
		group.setPolicy(TriggerGroupPolicyType.Some);
		trigger.setType(TriggerType.group);

		group.setNumber(3);
		trigger.setWhen(WhenType.CONDITION_FALSE_TO_TRUE);
//		trigger.setPreviousResult(false);


		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);

		ruleLoader.addOrUpdateData(group);


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
