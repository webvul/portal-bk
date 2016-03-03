package com.kii.extension.ruleengine.rule;


import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.drools.CommandExec;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestSimpleTrigger extends TestInit{




	@Autowired
	private CommandExec  exec;

	@Autowired
	private TriggerRecordDao dao;



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


		String triggerID=dao.addKiiEntity(record);


		engine.createSimpleTrigger(kiiThingID,triggerID,perdicate);

		sendGoodThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendBadThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendGoodThingStatus(kiiThingID);

		assertEquals(2,exec.getHitCount(triggerID));



	}
}
