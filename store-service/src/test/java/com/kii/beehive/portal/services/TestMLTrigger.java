package com.kii.beehive.portal.services;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.StoreServiceTestInit;
import com.kii.beehive.business.ruleengine.TriggerConditionBuilder;
import com.kii.beehive.business.ruleengine.TriggerConvertTool;
import com.kii.beehive.portal.store.entity.MLTriggerCombine;
import com.kii.beehive.portal.store.entity.trigger.Condition;
import com.kii.beehive.portal.store.entity.trigger.RuleEnginePredicate;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SingleThing;
import com.kii.beehive.portal.store.entity.trigger.WhenType;

public class TestMLTrigger extends StoreServiceTestInit{
	
	@Autowired
	private CombineTriggerService service;
	
	@Autowired
	private TriggerConvertTool convertTool;
	
	@Test
	public void addTrigger(){
		
		
		MLTriggerCombine combine=new MLTriggerCombine();
		
		SimpleTriggerRecord record=new SimpleTriggerRecord();
		SingleThing thing=new SingleThing();
		thing.setUserID("abc");
		
		record.setSource(thing);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setTriggersWhen(WhenType.CONDITION_CHANGED);
		Condition condition= TriggerConditionBuilder.andCondition().equal("foo","$p{ml.foo}").equal("ml.bar",2).getConditionInstance();
		predicate.setCondition(condition);
		record.setPredicate(predicate);
		
		combine.setBusinessTrigger(record);
		
		combine.setMlTaskID("demo");
		
//		MultipleSrcTriggerRecord newTrigger=convertTool.convertTrigger(combine.getBusinessTrigger());
		
//		System.out.println(newTrigger.getPredicate().getExpress());
		
		
		service.createTriggerWithML(combine);
		
	}
	
}
