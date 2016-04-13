package com.kii.extension.ruleengine.period;


import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.store.trigger.SchedulePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:ruleEngineCtx.xml"})
public class TestStart {

	@Autowired
	private ScheduleService  scheduleService;


	@Test
	public void testStartCron() throws SchedulerException, IOException {

		SchedulePeriod  period=new SchedulePeriod();
		period.setStartCron("0 30 17 * * ?");
		period.setEndCron("0 0 6 * * ?");

		scheduleService.addManagerTaskForSchedule("abc",period);

		System.in.read();

	}
}
