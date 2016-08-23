package com.kii.extension.ruleengine.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import javax.annotation.PreDestroy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.SchedulePeriod;
import com.kii.extension.ruleengine.store.trigger.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;

@Component
public class ScheduleService {
	
	
	public static final String TRIGGER_ID = "triggerID";
	private static final String STOP_PRE = "Stop";
	private static final String START_PRE = "Start";

	@Autowired
	private Scheduler scheduler;

//	@PostConstruct
	public void startSchedule(){

		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	@PreDestroy
	public void stop(){
		
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);

		}
	}


	public Map<String,Object> dump(){

		GroupMatcher<TriggerKey> any= GroupMatcher.anyTriggerGroup();

		Map<String,Object> triggerMap=new HashMap<>();

		try {
			Set<TriggerKey> keys = scheduler.getTriggerKeys(any);

			keys.forEach((k) -> {
				try {
					Trigger trigger = scheduler.getTrigger(k);
					Map<String,Object> data=new HashMap<>();
					data.put("endTime",trigger.getEndTime());
					data.put("startTime",trigger.getStartTime());

					data.put("nextFireTime",trigger.getNextFireTime());
					data.put("previousFireTime",trigger.getPreviousFireTime());

					data.put("jobType",k.getGroup());

					triggerMap.put(k.toString(), data);


				} catch (SchedulerException e) {
					e.printStackTrace();
				}

			});
		}catch(SchedulerException e){
			e.printStackTrace();
		}
		return triggerMap;
	}

	

	/**
	 * for reInit
	 */
	public void clearTrigger(){

		try {
			Set<TriggerKey>  triggers = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());

			triggers.forEach(triggerKey -> {
				try {
					scheduler.unscheduleJob(triggerKey);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}



	public void removeManagerTaskForSchedule(String triggerID) {
		try {
			scheduler.unscheduleJob(TriggerKey.triggerKey(triggerID,START_PRE));

			scheduler.unscheduleJob(TriggerKey.triggerKey(triggerID,STOP_PRE));

		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void addManagerTaskForSchedule(String triggerID, SchedulePeriod period) throws SchedulerException {



		Trigger triggerStart= TriggerBuilder.newTrigger()
				.withIdentity(TriggerKey.triggerKey(triggerID,START_PRE))
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngScheduleFactory.START_JOB)
				.withSchedule(cronSchedule(period.getStartCron()))
				.build();



		scheduler.scheduleJob(triggerStart);

		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.withIdentity(TriggerKey.triggerKey(triggerID,STOP_PRE))
				.usingJobData(TRIGGER_ID,triggerID)
				.forJob(RuleEngScheduleFactory.STOP_JOB)
				.withSchedule(cronSchedule(period.getEndCron()))
				.build();


		scheduler.scheduleJob(triggerEnd);

		Date nextStop=triggerEnd.getNextFireTime();
		Date nextStart=triggerStart.getNextFireTime();

		//fire miss trigger by hand
		if(nextStart.getTime()>=nextStop.getTime() ){
			JobDataMap  dataMap=scheduler.getJobDetail(RuleEngScheduleFactory.START_JOB).getJobDataMap();
			dataMap.put(TRIGGER_ID, triggerID);
			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB,dataMap);
		}else{
			JobDataMap  dataMap=scheduler.getJobDetail(RuleEngScheduleFactory.STOP_JOB).getJobDataMap();
			dataMap.put(TRIGGER_ID, triggerID);

			scheduler.triggerJob(RuleEngScheduleFactory.STOP_JOB,dataMap);
		}
	}

	public void addManagerTaskForSimple(String triggerID, SimplePeriod period) throws SchedulerException {

		long now=System.currentTimeMillis();

		//the trigger had finished
		if(period.getEndTime()<now){
			return;
		}

		if(period.getStartTime()>=now){
			Trigger triggerStart = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey(triggerID,START_PRE))
					.usingJobData(TRIGGER_ID, triggerID)
					.forJob(RuleEngScheduleFactory.START_JOB)
					.startAt(new Date(period.getStartTime()))
					.build();


			scheduler.scheduleJob(triggerStart);
		}else if(period.getEndTime()>=now){
			//fire miss start job by hand
			JobDataMap  dataMap=scheduler.getJobDetail(RuleEngScheduleFactory.START_JOB).getJobDataMap();
			dataMap.put(TRIGGER_ID, triggerID);
			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB,dataMap);
		}


		Trigger triggerEnd = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey(triggerID,STOP_PRE))
					.usingJobData(TRIGGER_ID, triggerID)
					.forJob(RuleEngScheduleFactory.STOP_JOB)
					.startAt(new Date(period.getEndTime()))
					.build();


		scheduler.scheduleJob(triggerEnd);
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
