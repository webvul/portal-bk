package com.kii.extension.ruleengine.rule;

import static junit.framework.TestCase.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.kii.extension.sdk.entity.thingif.ThingStatus;

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
		Condition condition= TriggerConditionBuilder.andCondition().great("one.foo",0).less("one.bar",0).great("two",100).great("three",3).getConditionInstance();
		predicate.setCondition(condition);
		predicate.setTriggersWhen(WhenType.CONDITION_TRUE);

		record.setPredicate(predicate);

		ThingSource  thing=new ThingSource();
//		thing.addStateName("foo");
//		thing.addStateName("bar");

		record.addSource("one",thing);

		GroupSummarySource group=new GroupSummarySource();
		Condition groupCond=TriggerConditionBuilder.newCondition().great("foo",0).getConditionInstance();

		group.setTheCondition(groupCond);
		group.setFunction(SummaryFunctionType.sum);
		group.setStateName("bar");

		record.addSource("two",group);

		GroupSummarySource summary=new GroupSummarySource();
		summary.setStateName("foo");
		summary.setTheCondition(groupCond);
		summary.setFunction(SummaryFunctionType.count);

		record.addSource("three",summary);

		Map<String,Set<String>> thingSet=new HashMap<>();

		List<String> thList=Arrays.asList("thing-104","thing-105","thing-106","thing-107");

		thingSet.put("three",new HashSet<>(thList));
		thingSet.put("two",new HashSet<>(thList));
		thingSet.put("one",new HashSet<>(Arrays.asList("thing-100")));

		engine.createMultipleSourceTrigger(record,thingSet);

		setFooBarStatus(100,-100,"thing-100");

		int i=0;
		for(String th:thList){

			setFooBarStatus(100+i,200+i,th);
			i++;
		}

		assertEquals(1,exec.getHitCount(triggerID));

	}

	private void setFooBarStatus(Object val,Object val2,String thingID){

		ThingStatus status=new ThingStatus();
		status.setField("foo",val);
		status.setField("bar",val2);

		engine.updateThingStatus(thingID,status,new Date());

	}




}
