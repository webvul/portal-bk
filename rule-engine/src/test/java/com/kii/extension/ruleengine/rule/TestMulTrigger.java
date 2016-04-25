package com.kii.extension.ruleengine.rule;

import static junit.framework.TestCase.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;

public class TestMulTrigger extends TestInit {



	@Autowired
	private EngineService  engine;


	@Test
	public void testMuiTrigger(){


		MultipleSrcTriggerRecord record=new MultipleSrcTriggerRecord();

		record.setRecordStatus(TriggerRecord.StatusType.enable);

		String triggerID="500";

		record.setId(triggerID);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().equal("one.foo",1).equal("two",2).equal("three",3).getConditionInstance();
		predicate.setCondition(condition);
		predicate.setTriggersWhen(WhenType.CONDITION_TRUE);

		record.setPredicate(predicate);

		ThingSource  thing=new ThingSource();
		thing.setStateName("foo");

		record.addSource("one",thing);

		GroupSummarySource group=new GroupSummarySource();
		Condition groupCond=TriggerConditionBuilder.newCondition().great("foo",0).getConditionInstance();

		group.setCondition(groupCond);
		group.setFunction(SummaryFunctionType.sum);
		group.setStateName("bar");

		record.addSource("two",group);

		GroupSummarySource summary=new GroupSummarySource();
		summary.setStateName("foo");
		summary.setFunction(SummaryFunctionType.sum);

		record.addSource("three",summary);

		Map<String,Set<String>> thingSet=new HashMap<>();

		Set<String> things=new HashSet<>();
		thingSet.put("three",new HashSet<>(Arrays.asList("thing-104","thing-105","thing-106","thing-107")));
		thingSet.put("two",new HashSet<>(Arrays.asList("thing-104","thing-105","thing-106","thing-107")));
		thingSet.put("one",new HashSet<>(Arrays.asList("thing-100")));



		engine.createMultipleSourceTrigger(record,thingSet);





		assertEquals(0,exec.getHitCount(triggerID));


	}


}
