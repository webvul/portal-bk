package com.kii.extension.ruleengine.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.RuleEngineConfig;
import com.kii.extension.ruleengine.store.trigger.SchedulePeriod;
import com.kii.extension.ruleengine.store.trigger.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;

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

	public Map<String,Object> dump(){

		GroupMatcher<TriggerKey>  any= GroupMatcher.anyTriggerGroup();

		Map<String,Object> triggerMap=new HashMap<>();

		try {
			Set<TriggerKey> keys = scheduler.getTriggerKeys(any);

			keys.forEach((k) -> {
				try {
					Trigger trigger = scheduler.getTrigger(k);
					Map<String,Object> data=new HashMap<String, Object>();
					data.put("endTime",trigger.getEndTime());
					data.put("startTime",trigger.getStartTime());

					data.put("nextFireTime",trigger.getNextFireTime());
					data.put("previousFireTime",trigger.getPreviousFireTime());

					data.put("jobType",trigger.getJobKey().getName());
					data.put("triggerID",trigger.getJobDataMap().getString("triggerID"));

					triggerMap.put(trigger.getKey().getName(), data);


				} catch (SchedulerException e) {
					e.printStackTrace();
				}

			});
		}catch(SchedulerException e){
			e.printStackTrace();
		}
		return triggerMap;
	}

	public void addManagerTaskForSchedule(String triggerID, SchedulePeriod period) throws SchedulerException {


		Trigger triggerStart= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.START_JOB)
				.withSchedule(cronSchedule(period.getStartCron()))
				.build();



		scheduler.scheduleJob(triggerStart);

		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngineConfig.STOP_JOB)
				.withSchedule(cronSchedule(period.getEndCron()))
				.build();


		scheduler.scheduleJob(triggerEnd);

		Date nextStop=triggerEnd.getNextFireTime();
		Date nextStart=triggerStart.getNextFireTime();

		if(nextStart.getTime()>=nextStop.getTime()){
			scheduler.triggerJob(RuleEngineConfig.START_JOB);
		}else{
			scheduler.triggerJob(RuleEngineConfig.STOP_JOB);
		}
	}

	public void addManagerTaskForSimple(String triggerID, SimplePeriod period) throws SchedulerException {

		long now=System.currentTimeMillis();

		if(period.getStartTime()>=now){
			Trigger triggerStart = TriggerBuilder.newTrigger()
					.usingJobData(TRIGGER_ID, triggerID)
					.forJob(RuleEngineConfig.START_JOB)
					.startAt(new Date(period.getStartTime()))
					.build();

			scheduler.scheduleJob(triggerStart);
		}else if(period.getEndTime()>=now){
			scheduler.triggerJob(RuleEngineConfig.START_JOB);
		}

		if(period.getEndTime()>=now) {
			Trigger triggerEnd = TriggerBuilder.newTrigger()
					.usingJobData(TRIGGER_ID, triggerID)
					.forJob(RuleEngineConfig.STOP_JOB)
					.startAt(new Date(period.getEndTime()))
					.build();

			scheduler.scheduleJob(triggerEnd);
		}else{
			scheduler.triggerJob(RuleEngineConfig.STOP_JOB);
		}
	}
	
	
	public void addManagerTask(String triggerID,TriggerValidPeriod period) throws SchedulerException {


		if(period instanceof SimplePeriod){
			addManagerTaskForSimple(triggerID,(SimplePeriod)period);
		}else if(period instanceof SchedulePeriod){
			addManagerTaskForSchedule(triggerID,(SchedulePeriod)period);
		}else{
			throw new IllegalArgumentException("invalid period type");
		}

	}
}
