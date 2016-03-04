package com.kii.extension.ruleengine.rule;

import static junit.framework.TestCase.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicy;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicyType;
import com.kii.extension.ruleengine.store.trigger.TriggerSource;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestGroupTrigger extends TestInit {


	@Autowired
	private ThingTagManager tagService;

	@Test
	public void testGroupTrigger(){


		GroupTriggerRecord record=new GroupTriggerRecord();


		TagSelector  selector=new TagSelector();
		selector.addTag("Location-2F-room1");
		selector.addTag("Location-2F_room1");
		selector.setAndExpress(false);
		
		TriggerSource source=new TriggerSource();
		source.setSelector(selector);
		record.setSource(source);

		record.addTarget(getTarget() );

		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);
		record.setPredicate(perdicate);
		
		TriggerGroupPolicy policy=new TriggerGroupPolicy();
		policy.setGroupPolicy(TriggerGroupPolicyType.Any);
		record.setPolicy(policy);


		String triggerID=dao.addKiiEntity(record);


		Set<String> thingIDs=tagService.getKiiThingIDs(selector);

		engine.createGroupTrigger(thingIDs,policy,triggerID,perdicate);

		thingIDs.forEach(id->sendBadThingStatus(id));

		assertEquals(0,exec.getHitCount(triggerID));

		sendGoodThingStatus(thingIDs.iterator().next());

		assertEquals(1,exec.getHitCount(triggerID));

		sendBadThingStatus(thingIDs.iterator().next());

		assertEquals(1,exec.getHitCount(triggerID));

		sendGoodThingStatus(thingIDs.iterator().next());

		assertEquals(2,exec.getHitCount(triggerID));


	}
}
