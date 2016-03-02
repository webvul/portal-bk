package com.kii.extension.schedule;


import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.store.trigger.SchedulePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:scheduleCtx.xml"})
public class TestSchedule {

	@Autowired
	private ScheduleService  scheduleService;


	@Test
	public void testStart() throws SchedulerException, IOException {
		
		
		SchedulePeriod period=new SchedulePeriod();
		period.setEndCron("0  0/1 *  *  *  ?");
		period.setStartCron("0  5/1 *  *  *  ?");



		scheduleService.addManagerTaskForSchedule("aaa",period);

		System.in.read();

	}


}
