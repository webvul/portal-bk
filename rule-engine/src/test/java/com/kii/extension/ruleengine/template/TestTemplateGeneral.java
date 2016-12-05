package com.kii.extension.ruleengine.template;

import java.util.ArrayList;
import java.util.List;

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
import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.store.trigger.CommandParam;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.schedule.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
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

		List<ExternalValues> extList=new ArrayList<>();
		ExternalValues value1=new ExternalValues("demo");
		value1.addValue("aaa",123);
		value1.addValue("bbb",321);
		value1.addValue("ccc","abc");
	    extList.add(value1);

		List<CommandParam> paramList=new ArrayList<>();
		CommandParam param1=new CommandParam();
		param1.setExpress("$p{foo}");
		param1.setName("s_foo");
		paramList.add(param1);

		CommandParam param2=new CommandParam();
		param2.setExpress("$e{bar}");
		param2.setName("e_foo");
		paramList.add(param2);

		CommandParam param3=new CommandParam();
		param3.setExpress("$p{abc}");
		param3.setName("p_abc");
		paramList.add(param3);

		log.info(general.getSimpleTriggerDrl("abcSchedule",predicate,paramList));

		predicate.setSchedule(null);

		log.info(general.getSimpleTriggerDrl("abc",predicate,paramList));

//		log.info(general.getSimpleTriggerDrl("abc", TriggerType.summary,predicate,paramList));

		predicate.setSchedule(prefix);
//		log.info(general.getSimpleTriggerDrl("abcSchedule", TriggerType.summary,predicate,paramList));


//		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.Any,predicate));
//
//		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.All,predicate));
//
//		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.Percent,predicate));
//
//		log.info(general.generGroupDrlConfig("abcGroup", TriggerGroupPolicyType.Some,predicate));


		predicate.setCondition(null);

		log.info(general.getSimpleTriggerDrl("schedule",predicate,new ArrayList<>()));



	}
}
