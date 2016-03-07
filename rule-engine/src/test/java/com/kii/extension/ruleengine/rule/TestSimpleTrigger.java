package com.kii.extension.ruleengine.rule;


import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestSimpleTrigger extends TestInit{





	@Test
	public void testFalseToTrue(){

		String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

		SimpleTriggerRecord record=new SimpleTriggerRecord();

		SimpleTriggerRecord.ThingID thingID=new SimpleTriggerRecord.ThingID();
		thingID.setThingID(1049);

		record.setSource(thingID);

		record.addTarget(getTarget() );

		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_FALSE_TO_TRUE);


		record.setPredicate(perdicate);


		String triggerID="100";
		record.setId(triggerID);

		engine.createSimpleTrigger(kiiThingID,triggerID,perdicate);

		sendBadThingStatus(kiiThingID);
		sendGoodThingStatus(kiiThingID);


		assertEquals(1,exec.getHitCount(triggerID));

		sendBadThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendGoodThingStatus(kiiThingID);

		assertEquals(2,exec.getHitCount(triggerID));


	}


	@Test
	public void testSimpleTrigger(){

		String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

		SimpleTriggerRecord record=new SimpleTriggerRecord();

		SimpleTriggerRecord.ThingID thingID=new SimpleTriggerRecord.ThingID();
		thingID.setThingID(1049);

		record.setSource(thingID);

		record.addTarget(getTarget() );
		
		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);


		record.setPredicate(perdicate);


		String triggerID="101";
		record.setId(triggerID);


		engine.createSimpleTrigger(kiiThingID,triggerID,perdicate);

		sendGoodThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendBadThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendGoodThingStatus(kiiThingID);

		assertEquals(2,exec.getHitCount(triggerID));



	}
}