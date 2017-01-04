package com.kii.extension.ruleengine.rule;


import static junit.framework.TestCase.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestSimpleTrigger extends TestInit{





	@Test
	public void testFalseToTrue(){

		String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

		SimpleTriggerRecord record=new SimpleTriggerRecord();

		SingleThing thingID=new SingleThing();
		
		thingID.setThingID(1049l);
		
		record.setSource(thingID);

		record.addTarget(getTarget() );

		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_FALSE_TO_TRUE);


		record.setPredicate(perdicate);
		record.setRecordStatus(TriggerRecord.StatusType.enable);


		String triggerID="100";
		record.setId(triggerID);

		Map<String,Set<String>> map=new HashMap<>();

		map.put("comm", Collections.singleton(kiiThingID));

		engine.addTriggerToEngine(record,map,false);

//		engine.enableTrigger(triggerID);

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
		
		SingleThing thingID=new SingleThing();
		thingID.setThingID(1049l);

		record.setSource(thingID);

		record.addTarget(getTarget() );
		
		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);


		record.setPredicate(perdicate);


		String triggerID="101";
		record.setId(triggerID);
		record.setRecordStatus(TriggerRecord.StatusType.enable);


		engine.addTriggerToEngine(record,new HashMap<>(),false );

//		engine.enableTrigger(triggerID);

		sendGoodThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendBadThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendGoodThingStatus(kiiThingID);

		assertEquals(2,exec.getHitCount(triggerID));



	}
}
