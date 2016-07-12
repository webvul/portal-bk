package com.kii.extension.ruleengine.rule;

import static junit.framework.TestCase.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.common.manager.ThingTagManager;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SlideFuntion;
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
	public void testSummaryTrigger(){


		SummaryTriggerRecord record=new SummaryTriggerRecord();


		Map<String,Set<String>> thingMap=new HashMap<>();


		TagSelector selector=new TagSelector();
		selector.addTag("Location-2F-room1");
		selector.setAndExpress(false);
		
		SummarySource source=new SummarySource();
		source.setSource(selector);
		
		SummaryExpress exp1=new SummaryExpress();
		exp1.setFunction(SummaryFunctionType.sum);
		exp1.setStateName("foo");
		exp1.setSummaryAlias("foo_sum");

		source.addExpress(exp1);
		record.addSummarySource("source",source);
		thingMap.put("source",tagService.getKiiThingIDs(selector));

		TagSelector selector2=new TagSelector();
		selector2.addTag("Location-2F-room1");
		selector2.setAndExpress(false);

		SummarySource target=new SummarySource();
		target.setSource(selector2);
		SummaryExpress exp2=new SummaryExpress();
		exp2.setSummaryAlias("bar_sum");
		exp2.setFunction(SummaryFunctionType.sum);
		exp2.setStateName("bar");
		target.addExpress(exp2);

		record.addSummarySource("target",target);
		thingMap.put("target",tagService.getKiiThingIDs(selector2));


		record.addTarget(getTarget() );


		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().greatExp("source.foo_sum","$s{target.bar_sum}").getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);
		record.setPredicate(perdicate);


		String triggerID="300";
		record.setId(triggerID);

		engine.createSummaryTrigger(record,thingMap);

		engine.enableTrigger(triggerID);

		Set<String> thingIDs=new HashSet<>();

		thingIDs.addAll(thingMap.get("source"));
		thingIDs.addAll(thingMap.get("target"));

		thingIDs.forEach(id->sendGoodThingStatus(id));

		assertEquals(4,exec.getHitCount(triggerID));

		thingIDs.forEach(id->sendBadThingStatus(id));

		assertEquals(5,exec.getHitCount(triggerID));



	}

	@Test
	public void testSummarySlideTrigger(){


		SummaryTriggerRecord record=new SummaryTriggerRecord();


		Map<String,Set<String>> thingMap=new HashMap<>();


		SummaryExpress exp1 = getExpSource();

		TagSelector selector=new TagSelector();
		selector.addTag("Location-2F-room1");
		selector.setAndExpress(false);

		SummarySource source=new SummarySource();
		source.setSource(selector);
		source.addExpress(exp1);
		record.addSummarySource("source",source);
		thingMap.put("source",tagService.getKiiThingIDs(selector));

		TagSelector selector2=new TagSelector();
		selector2.addTag("Location-2F-room1");
		selector2.setAndExpress(false);

		SummarySource target=new SummarySource();
		target.setSource(selector2);
		SummaryExpress exp2 = getTargetExpress();

		target.addExpress(exp2);

		record.addSummarySource("target",target);
		thingMap.put("target",tagService.getKiiThingIDs(selector2));


		record.addTarget(getTarget() );


		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().greatExp("source.foo_sum","$s{target.bar_sum}").getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);
		record.setPredicate(perdicate);


		String triggerID="301";
		record.setId(triggerID);

//		engine.createStreamSummaryTrigger(record,thingMap);

		engine.enableTrigger(triggerID);

		Set<String> thingIDs=new HashSet<>();

		thingIDs.addAll(thingMap.get("source"));
		thingIDs.addAll(thingMap.get("target"));

		thingIDs.forEach(id->sendGoodThingStatus(id));

		assertEquals(4,exec.getHitCount(triggerID));

		thingIDs.forEach(id->sendBadThingStatus(id));

		assertEquals(5,exec.getHitCount(triggerID));



	}

	private SummaryExpress getTargetExpress() {
		SummaryExpress exp2=new SummaryExpress();
		exp2.setSummaryAlias("bar_sum");
		exp2.setFunction(SummaryFunctionType.sum);
		exp2.setStateName("bar");

		SlideFuntion slide2=new SlideFuntion();
		slide2.setLength(1);
		slide2.setType(SlideFuntion.SlideType.length);
		exp2.setSlideFuntion(slide2);
		return exp2;
	}

	private SummaryExpress getExpSource() {
		SummaryExpress exp1=new SummaryExpress();
		exp1.setFunction(SummaryFunctionType.sum);
		SlideFuntion slide1=new SlideFuntion();
		slide1.setLength(3);
		slide1.setType(SlideFuntion.SlideType.length);
		exp1.setSlideFuntion(slide1);

		exp1.setStateName("foo");
		exp1.setSummaryAlias("foo_sum");
		return exp1;
	}


}
