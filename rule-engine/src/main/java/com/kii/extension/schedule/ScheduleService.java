package com.kii.extension.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.Date;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.store.trigger.SchedulePeriod;
import com.kii.extension.store.trigger.SimplePeriod;
import com.kii.extension.RuleEngineConfig;

@Component
public class ScheduleService {
	
	
	public static final String TRIGGER_ID = "triggerID";

	@Autowired
	private Scheduler scheduler;

	@PostConstruct
	public void init(){

		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void stop(){
		
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void addManagerTaskForSchedule(String triggerID, SchedulePeriod period) throws SchedulerException {


		Trigger triggerStart= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.START_JOB,RuleEngineConfig.MANAGER_GROUP)
				.withSchedule(cronSchedule(period.getStartCron()))
				.build();

		scheduler.scheduleJob(triggerStart);


		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.STOP_JOB,RuleEngineConfig.MANAGER_GROUP)
				.withSchedule(cronSchedule(period.getEndCron()))
				.build();

		scheduler.scheduleJob(triggerEnd);
	}

	public void addManagerTaskForSimple(String triggerID, SimplePeriod period) throws SchedulerException {


		Trigger triggerStart= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.START_JOB,RuleEngineConfig.MANAGER_GROUP)
				.startAt(new Date(period.getStartTime()))
				.build();

		scheduler.scheduleJob(triggerStart);


		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.STOP_JOB, RuleEngineConfig.MANAGER_GROUP)
				.startAt(new Date(period.getEndTime()))
				.build();

		scheduler.scheduleJob(triggerEnd);
	}



}
