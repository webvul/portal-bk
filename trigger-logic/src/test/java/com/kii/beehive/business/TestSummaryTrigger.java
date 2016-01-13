package com.kii.beehive.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.kii.beehive.portal.manager.ThingStateSummaryManager;
import com.kii.beehive.portal.store.entity.trigger.SummaryExpress;
import com.kii.beehive.portal.store.entity.trigger.SummaryFunctionType;
import com.kii.beehive.portal.store.entity.trigger.SummarySource;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;

public class TestSummaryTrigger extends TestTemplate {

	private Logger log= LoggerFactory.getLogger(TestSummaryTrigger.class);

	@Autowired
	private ThingStateSummaryManager  mang;


	private Long[] thingIDs={575l,576l,577l,578l,579l,580l,581l,582l,583l,584l};

	private String appName="b8ca23d0";

	private Long[] tags={311l,312l,313l,314l,315l};

	private String[] tagNames={"Custom-name0","Custom-name1","Custom-name2","Custom-name3","Custom-name4"};



	@Test
	public void createTrigger() throws IOException {


		SummaryTriggerRecord  record=new SummaryTriggerRecord();

		record.addSummarySource("source",getSource1());
		record.addSummarySource("target",getSource2());
		
		StatePredicate perdicate=new StatePredicate();
		perdicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);

		Condition cond= ConditionBuilder.newCondition().equal("source.sum_number","$(target.sum_num)").getConditionInstance();
		perdicate.setCondition(cond);

		record.setPerdicate(perdicate);

		record.addTarget(getTagCmdTarget());


		String json=mapper.writeValueAsString(record);


		log.info(json);

		TriggerRecord rec=mapper.readValue(json,TriggerRecord.class);


		mang.initStateSummary((SummaryTriggerRecord)rec);


	}


	private TriggerTarget getTagCmdTarget() {
		TriggerTarget target=new TriggerTarget();

		TagSelector selector=new TagSelector();

		selector.addTag(tagNames[2]);
		selector.addTag(tagNames[3]);
		selector.setAndExpress(true);

		TargetAction action = getTargetAction("powerOn","power",true);
		target.setCommand(action);
		return target;
	}

	private TargetAction getTargetAction(String name,String actName,Object value) {
		TargetAction action=new TargetAction();
		ThingCommand cmd=new ThingCommand();

		Action act=new Action();
		act.setField(actName,value);

		cmd.addAction(name,act);
		action.setCommand(cmd);
		return action;
	}

	private SummarySource getSource2() {
		SummarySource source1=new SummarySource();
		TriggerSource s1=new TriggerSource();
		TagSelector sele1=new TagSelector();
		sele1.addTag("Custom-name2");
		sele1.addTag("Custom-name3");
		s1.setSelector(sele1);
		source1.setSource(s1);

		List<SummaryExpress> expList=new ArrayList<>();
		SummaryExpress exp1=new SummaryExpress();
		exp1.setFunction(SummaryFunctionType.Sum);
		exp1.setStateName("foo");
		exp1.setSummaryAlias("sum_num");
		expList.add(exp1);

		SummaryExpress exp2=new SummaryExpress();
		exp2.setFunction(SummaryFunctionType.Avg);
		exp2.setStateName("bar");
		exp2.setSummaryAlias("avg_num");

		expList.add(exp2);
		source1.setExpressList(expList);

		return source1;
	}

	private SummarySource getSource1() {
		SummarySource source1=new SummarySource();
		TriggerSource s1=new TriggerSource();
		TagSelector sele1=new TagSelector();
		sele1.addTag("Custom-name0");
		sele1.addTag("Custom-name1");
		s1.setSelector(sele1);
		source1.setSource(s1);

		List<SummaryExpress> expList=new ArrayList<>();
		SummaryExpress exp1=new SummaryExpress();
		exp1.setFunction(SummaryFunctionType.Sum);
		exp1.setStateName("foo");
		exp1.setSummaryAlias("sum_number");
		expList.add(exp1);

		SummaryExpress exp2=new SummaryExpress();
		exp2.setFunction(SummaryFunctionType.Avg);
		exp2.setStateName("bar");
		exp2.setSummaryAlias("avg_number");

		expList.add(exp2);
		source1.setExpressList(expList);

		return source1;
	}

}
