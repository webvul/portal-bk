package com.kii.extension.ruleengine.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.Date;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.trigger.SchedulePeriod;
import com.kii.beehive.portal.store.entity.trigger.SimplePeriod;
import com.kii.extension.ruleengine.RuleEngineConfig;
import com.kii.extension.sdk.entity.thingif.Predicate;
import com.kii.extension.sdk.entity.thingif.SchedulePredicate;
import com.kii.extension.sdk.entity.thingif.TaskPredicate;

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

	public void addExecuteTask(String triggerID, Predicate predicate) throws SchedulerException {

		TriggerBuilder  builder= TriggerBuilder.newTrigger()
				.startNow()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.EXECUTE_JOB,RuleEngineConfig.EXE_GROUP);

		Trigger trigger=null;

		if(predicate instanceof  SchedulePredicate) {
			SchedulePredicate  sch=(SchedulePredicate)predicate;

			trigger=builder.withSchedule(cronSchedule(sch.getCron()))
					.build();
		}

		if(predicate instanceof  TaskPredicate) {
			TaskPredicate  task=(TaskPredicate)predicate;

			trigger=builder.startAt(new Date(task.getTimestamp()))
					.build();
		}

		if(trigger!=null) {
			scheduler.scheduleJob(trigger);
		}
	}

	public void addManagerTaskForSchedule(String triggerID, SchedulePeriod period) throws SchedulerException {


		Trigger triggerStart= TriggerBuilder.newTrigger()
				.startNow()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.START_JOB,RuleEngineConfig.MANAGER_GROUP)
				.withSchedule(cronSchedule(period.getStartCron()))
				.build();

		scheduler.scheduleJob(triggerStart);


		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.startNow()
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
				.startAt(new Date(period.getStartAt()))
				.withSchedule(simpleSchedule()
						.withIntervalInMinutes(period.getInterval()))
				.build();

		scheduler.scheduleJob(triggerStart);


		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.STOP_JOB, RuleEngineConfig.MANAGER_GROUP)
				.startAt(new Date(period.getStartAt()+period.getDuration()))
				.withSchedule(simpleSchedule().withIntervalInMinutes(period.getInterval()))
				.build();

		scheduler.scheduleJob(triggerEnd);
	}



}
