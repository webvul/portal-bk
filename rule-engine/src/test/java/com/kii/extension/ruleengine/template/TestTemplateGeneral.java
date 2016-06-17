package com.kii.extension.ruleengine.template;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicyType;
import com.kii.extension.ruleengine.store.trigger.WhenType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:./SimpleCtx.xml"})
public class TestTemplateGeneral {


	private Logger log= LoggerFactory.getLogger(TestTemplateGeneral.class);

	@Autowired
	private RuleGeneral  general;

	private ObjectMapper mapper=new ObjectMapper();

	@Test
	public void testSimple() throws JsonProcessingException {


		Condition condition= TriggerConditionBuilder.newCondition().equal("foo","true").getConditionInstance();

		log.info(mapper.writeValueAsString(condition));


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

		log.info(general.generDrlConfig("abcSchedule", TriggerType.simple,predicate,new ArrayList<>()));

		predicate.setSchedule(null);

		log.info(general.generDrlConfig("abc", TriggerType.simple,predicate,new ArrayList<>()));

		log.info(general.generDrlConfig("abc", TriggerType.summary,predicate,new ArrayList<>()));

		predicate.setSchedule(prefix);
		log.info(general.generDrlConfig("abcSchedule", TriggerType.summary,predicate,new ArrayList<>()));


		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.Any,predicate));

		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.All,predicate));

		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.Percent,predicate));

		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.Some,predicate));


		predicate.setCondition(null);

		log.info(general.generDrlConfig("schedule", TriggerType.simple,predicate,new ArrayList<>()));



	}
}
