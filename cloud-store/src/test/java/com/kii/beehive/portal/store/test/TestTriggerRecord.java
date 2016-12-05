package com.kii.beehive.portal.store.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.store.trigger.target.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.target.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.schedule.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.schedule.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TestTriggerRecord {

	private ObjectMapper mapper=new ObjectMapper();

	@Before
	public void init(){

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//		mapper.configure(Co)
	}


	@Test
	public void testSimple() throws IOException {

		SimpleTriggerRecord  record=new SimpleTriggerRecord();
		record.setName("test");
		record.addTargetParam("foo","$p{foo}");

		TagSelector selector=new TagSelector();
		selector.addTag("tag_a");
		selector.addTag("tag_b");

		CommandToThing cmd=new CommandToThing();
		cmd.setSelector(selector);

		ThingCommand thingCmd=new ThingCommand();
		Action action=new Action();
		action.setField("power","ON");
		thingCmd.addAction("foo",action);

		cmd.setCommand(thingCmd);

		record.addTarget(cmd);

		CallHttpApi  api=new CallHttpApi();
		api.setContent("{\"foo\":\"bar\",\"name\":\"abc\",\"val\":123}");
		api.setUrl("http://localhost");
		api.setMethod(CallHttpApi.HttpMethod.POST);

		record.addTarget(api);

		record.setThingID(123l);
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();

		CronPrefix  prefix=new CronPrefix();
		prefix.setCron("1 1 1 * * ");
		predicate.setSchedule(prefix);
		record.setPredicate(predicate);
		
		SimplePeriod prepared=new SimplePeriod();
		prepared.setStartTime(100l);
		prepared.setEndTime(101l);

		record.setPreparedCondition(prepared);


		String json=mapper.writeValueAsString(record);

		System.out.println(json);

		TriggerRecord  result=mapper.readValue(json,TriggerRecord.class);

		String  json2=mapper.writeValueAsString(result);

		System.out.println(json2);

	}



	@Test
	public void testMuiRecord() throws IOException {

		MultipleSrcTriggerRecord  record=new MultipleSrcTriggerRecord();
		record.setName("test");
		record.addTargetParam("foo","$p{foo}");

		TagSelector selector=new TagSelector();
		selector.addTag("tag_a");
		selector.addTag("tag_b");

		CommandToThing cmd=new CommandToThing();
		cmd.setSelector(selector);

		ThingCommand thingCmd=new ThingCommand();
		Action action=new Action();
		action.setField("power","ON");
		thingCmd.addAction("foo",action);

		cmd.setCommand(thingCmd);

		record.addTarget(cmd);

		CallHttpApi  api=new CallHttpApi();
		api.setContent("{\"foo\":\"bar\",\"name\":\"abc\",\"val\":123}");
		api.setUrl("http://localhost");
		api.setMethod(CallHttpApi.HttpMethod.POST);

		record.addTarget(api);
		
		GroupSummarySource summary1=new GroupSummarySource();
		summary1.setSource(selector);
		summary1.setStateName("foo");
		summary1.setFunction(SummaryFunctionType.count);
		Condition cond=  new Equal("foo","bar");
		summary1.setTheCondition(cond);


		GroupSummarySource summary2=new GroupSummarySource();
		summary2.setSource(selector);
		summary2.setStateName("foo");
		summary2.setFunction(SummaryFunctionType.count);
		summary1.setTheCondition(cond);

		record.addSource("two",summary2);
;
		RuleEnginePredicate predicate=new RuleEnginePredicate();

		CronPrefix  prefix=new CronPrefix();
		prefix.setCron("1 1 1 * * ");
		predicate.setSchedule(prefix);

		predicate.setExpress("$p{one}>$p{two}");

		record.setPredicate(predicate);

		SimplePeriod prepared=new SimplePeriod();
		prepared.setStartTime(100l);
		prepared.setEndTime(101l);

		record.setPreparedCondition(prepared);


		String json=mapper.writeValueAsString(record);

		System.out.println(json);

		TriggerRecord  result=mapper.readValue(json,TriggerRecord.class);

		String  json2=mapper.writeValueAsString(result);

		System.out.println(json2);

	}

}
