package com.kii.extension.schedule;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.beehive.portal.store.entity.trigger.SchedulePeriod;
import com.kii.extension.ruleengine.schedule.ScheduleService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:scheduleTestCtx.xml"})
public class TestSchedule {

	@Autowired
	private ScheduleService  scheduleService;


	@Test
	public void testStart() throws SchedulerException {
		
		
		SchedulePeriod period=new SchedulePeriod();
		period.setEndCron("0  0/5 *  *  *  ?");
		period.setStartCron("0  1/5 *  *  *  ?");



		scheduleService.addManagerTaskForSchedule("aaa",period);

	}


}
