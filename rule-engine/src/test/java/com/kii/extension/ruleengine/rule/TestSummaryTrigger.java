package com.kii.extension.ruleengine.rule;

import static junit.framework.TestCase.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SummaryExpress;
import com.kii.extension.ruleengine.store.trigger.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.SummarySource;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestSummaryTrigger extends  TestInit{



	@Autowired
	private ThingTagManager tagService;
	@Test
	public void testGroupTrigger(){


		SummaryTriggerRecord record=new SummaryTriggerRecord();


		Map<String,Set<String>> thingMap=new HashMap<>();


		TagSelector selector=new TagSelector();
		selector.addTag("Location-2F-room1");
		selector.setAndExpress(false);
		
		SummarySource source=new SummarySource();
		source.setSourceSelector(selector);
		
		SummaryExpress exp1=new SummaryExpress();
		exp1.setFunction(SummaryFunctionType.Sum);
		exp1.setStateName("foo");
		exp1.setSummaryAlias("foo_sum");

		source.addExpress(exp1);
		record.addSummarySource("source",source);
		thingMap.put("source",tagService.getKiiThingIDs(selector));

		TagSelector selector2=new TagSelector();
		selector2.addTag("Location-2F_room1");

		SummarySource target=new SummarySource();
		target.setSourceSelector(selector2);
		SummaryExpress exp2=new SummaryExpress();
		exp2.setSummaryAlias("bar_sum");
		exp2.setFunction(SummaryFunctionType.Sum);
		exp2.setStateName("bar");
		target.addExpress(exp2);

		record.addSummarySource("target",target);
		thingMap.put("target",tagService.getKiiThingIDs(selector2));


		record.addTarget(getTarget() );


		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().greatExp("source.sum_foo","#s{target.sum_bar}").getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);
		record.setPredicate(perdicate);

//		String triggerID=dao.addKiiEntity(record);

		String triggerID="300";
		record.setId(triggerID);

		engine.createSummaryTrigger(record,thingMap);


		Set<String> thingIDs=new HashSet<>();

		thingIDs.addAll(thingMap.get("source"));
		thingIDs.addAll(thingMap.get("target"));

		thingIDs.forEach(id->sendGoodThingStatus(id));

		assertEquals(1,exec.getHitCount(triggerID));

		thingIDs.forEach(id->sendBadThingStatus(id));

		assertEquals(1,exec.getHitCount(triggerID));



	}



}
