package com.kii.extension.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kii.extension.store.trigger.TriggerWhen;
import com.kii.extension.drools.entity.Summary;
import com.kii.extension.drools.entity.SummaryValueMap;
import com.kii.extension.drools.entity.Trigger;
import com.kii.extension.drools.entity.TriggerType;

public class TestSlideSummary extends InitTest{



	@Before
	public void init() throws IOException {


		ruleLoader.initCondition(
				getDrlContent("triggerComm"),
				getDrlContent("summaryCompute"),
				getDrlContent("slideSummary")
		);

		initGlobal();

	}

	@Test
	public void testTrigger300(){

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		Trigger trigger=new Trigger();
		for(int i=0;i<5;i++) {
			trigger.addThing(String.valueOf(i));
		}

		trigger.setType(TriggerType.summary);
		trigger.setWhen(TriggerWhen.CONDITION_FALSE_TO_TRUE);
//		trigger.setPreviousResult(false);

		String triggerID="400";
		trigger.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(trigger);

		Summary summary=new Summary();
		for(int i=0;i<5;i++) {
			summary.addThing(String.valueOf(i));
		}
		summary.setFieldName("foo");
		summary.setFunName("sum-length");
		summary.setSummaryField("sum_foo");
		summary.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(summary);

		Summary summary2=new Summary();
		for(int i=0;i<5;i++) {
			summary2.addThing(String.valueOf(i));
		}
		summary2.setFieldName("bar");
		summary2.setFunName("sum-time");
		summary2.setSummaryField("sum_bar");
		summary2.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(summary2);

		SummaryValueMap value=new SummaryValueMap();
		value.setTriggerID(triggerID);

		ruleLoader.addOrUpdateData(value);


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),i%2==0?paramOk:paramNo);
		}
		ruleLoader.fireCondition();

		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramOk);
			ruleLoader.fireCondition();
		}
		assertEquals(1,exec.getHitCount(triggerID));


		for(int i=0;i<5;i++){
			updateThingState(String.valueOf(i),paramNo);
			ruleLoader.fireCondition();
		}
		assertEquals(1,exec.getHitCount(triggerID));


	}

}
