package com.kii.beehive.portal.store.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.store.entity.trigger.Condition;
import com.kii.beehive.portal.store.entity.trigger.GroupSummarySource;
import com.kii.beehive.portal.store.entity.trigger.MultipleSrcTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.RuleEnginePredicate;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SingleThing;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.ThingCollectSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.condition.Equal;
import com.kii.beehive.portal.store.entity.trigger.groups.SummaryFunctionType;
import com.kii.beehive.portal.store.entity.trigger.schedule.CronPrefix;
import com.kii.beehive.portal.store.entity.trigger.schedule.SimplePeriod;
import com.kii.beehive.portal.store.entity.trigger.task.CallHttpApi;
import com.kii.beehive.portal.store.entity.trigger.task.CommandToThing;
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
		
		
		SingleThing th=new SingleThing();
		th.setThingID(123l);
		
		record.setSource(th);
		
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
		ThingCollectSource src=new ThingCollectSource();
		src.setSelector(selector);
		summary1.setSource(src);
		summary1.setStateName("foo");
		summary1.setFunction(SummaryFunctionType.count);
		Condition cond=  new Equal("foo","bar");
		summary1.setTheCondition(cond);


		GroupSummarySource summary2=new GroupSummarySource();
		
		ThingCollectSource src2=new ThingCollectSource();
		src2.setSelector(selector);
		summary2.setSource(src2);
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
