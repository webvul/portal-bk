package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.store.trigger.PreparedCondition;
import com.kii.extension.ruleengine.store.trigger.SchedulePeriod;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

public class TestTriggerRecord {

	private ObjectMapper mapper=new ObjectMapper();

	@Before
	public void init(){

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}


	@Test
	public void testCustom() throws IOException {

		TriggerRecord record=new SimpleTriggerRecord();

		record.setCustomProperty("foo","bar");

		record.setCustomProperty("abc",123);

		SchedulePeriod  period=new SchedulePeriod();
		period.setEndCron("a");
		period.setStartCron("b");

		PreparedCondition condition=new PreparedCondition();
		condition.setPeriod(period);

		record.setPreparedCondition(period);

		String json=mapper.writeValueAsString(record);


		System.out.println(json);

		record=mapper.readValue(json,SimpleTriggerRecord.class);


		assertEquals("bar",record.getCustom().getCustom().get("foo"));





	}
}
