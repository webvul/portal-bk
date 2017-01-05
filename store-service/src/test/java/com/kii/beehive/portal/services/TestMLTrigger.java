package com.kii.beehive.portal.services;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.StoreServiceTestInit;
import com.kii.beehive.portal.entitys.MLTriggerCombine;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.WhenType;

public class TestMLTrigger extends StoreServiceTestInit{
	
	@Autowired
	private MLTriggerService  service;
	
	
	@Test
	public void addTrigger(){
		
		
		MLTriggerCombine combine=new MLTriggerCombine();
		
		SimpleTriggerRecord record=new SimpleTriggerRecord();
		SingleThing thing=new SingleThing();
		thing.setUserID("abc");
		
		record.setSource(thing);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setTriggersWhen(WhenType.CONDITION_CHANGED);
		Condition condition= TriggerConditionBuilder.andCondition().equal("foo",1).equal("bar",2).getConditionInstance();
		predicate.setCondition(condition);
		record.setPredicate(predicate);
		
		combine.setBusinessTrigger(record);
		
		combine.setMlTaskID("demo");
		Condition mlCondition=TriggerConditionBuilder.orCondition().equal("value",2).great("abc",1).getConditionInstance();
		combine.setMlCondition(mlCondition);
		
		combine.setJoinWithAND(true);
		
		service.createTriggerWithML(combine);
		
		
	}
}
