package com.kii.extension.ruleengine.template;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.WhenType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:./SimpleCtx.xml"})
public class TestTemplateGeneral {


	private Logger log= LoggerFactory.getLogger(TestTemplateGeneral.class);

	@Autowired
	private RuleGeneral  general;

	@Test
	public void testSimple(){


		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();


		String express=general.generExpress(condition);

		log.info(express);

		CronPrefix prefix=new CronPrefix();

		prefix.setCron("0 0/1 * * * * ? ");

		String schedule=general.generTimer(prefix);

		log.info(schedule);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setTriggersWhen(WhenType.CONDITION_CHANGED);
		predicate.setCondition(condition);
		predicate.setSchedule(prefix);

		log.info(general.generDrlConfig("abcSchedule", TriggerType.simple,predicate));

		predicate.setSchedule(null);

		log.info(general.generDrlConfig("abc", TriggerType.simple,predicate));

		log.info(general.generDrlConfig("abc", TriggerType.summary,predicate));

		predicate.setSchedule(prefix);
		log.info(general.generDrlConfig("abcSchedule", TriggerType.summary,predicate));


	}
}
