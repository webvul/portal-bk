package com.kii.extension.ruleengine.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import javax.annotation.PreDestroy;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.IntervalPrefix;
import com.kii.extension.ruleengine.store.trigger.SchedulePeriod;
import com.kii.extension.ruleengine.store.trigger.SchedulePrefix;
import com.kii.extension.ruleengine.store.trigger.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;

@Component
public class ScheduleService {
	
	
	private static final String STOP_PRE = "Stop";
	private static final String START_PRE = "Start";

	public static final String EXEC_PRE="Exec";

	
	@Autowired
	private Scheduler scheduler;

	@PreDestroy
	public void stop(){
		
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);

		}
	}


	public Map<String,Object> dump(String triggerID){


		Map<String,Object> triggerMap=new HashMap<>();

		try {

			Set<TriggerKey> keys = new HashSet<>();

			if(triggerID==null) {
				GroupMatcher<TriggerKey> any= GroupMatcher.anyGroup();

				keys = scheduler.getTriggerKeys(any);
			}else{
				keys.add(TriggerKey.triggerKey(triggerID,START_PRE));
				keys.add(TriggerKey.triggerKey(triggerID,STOP_PRE));
				keys.add(TriggerKey.triggerKey(triggerID,EXEC_PRE));

			}

			keys.forEach((k) -> {
				try {
					Trigger trigger = scheduler.getTrigger(k);
					if(trigger==null){
						return;
					}
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

			scheduler.unscheduleJob(TriggerKey.triggerKey(triggerID,EXEC_PRE));

		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void addManagerTaskForSchedule(String triggerID, SchedulePeriod period,boolean isDrools) throws SchedulerException {



		Trigger triggerStart= TriggerBuilder.newTrigger()
				.withIdentity(TriggerKey.triggerKey(triggerID,START_PRE))
				.usingJobData(ProxyJob.TRIGGER_ID,triggerID)
				.usingJobData(ProxyJob.TYPE_SIGN,isDrools)
				.forJob(RuleEngScheduleFactory.START_JOB)
				.withSchedule(cronSchedule(period.getStartCron()))
				.build();


		scheduler.scheduleJob(triggerStart);

		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.withIdentity(TriggerKey.triggerKey(triggerID,STOP_PRE))
				.usingJobData(ProxyJob.TRIGGER_ID,triggerID)
				.usingJobData(ProxyJob.TYPE_SIGN,isDrools)
				.forJob(RuleEngScheduleFactory.STOP_JOB)
				.withSchedule(cronSchedule(period.getEndCron()))
				.build();


		scheduler.scheduleJob(triggerEnd);

		Date nextStop=triggerEnd.getNextFireTime();
		Date nextStart=triggerStart.getNextFireTime();

		JobDataMap  innMap=new JobDataMap();
		innMap.put(ProxyJob.TRIGGER_ID,triggerID);

		if(nextStart.getTime()>=nextStop.getTime() ){

			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB,innMap);
		}else{
			scheduler.triggerJob(RuleEngScheduleFactory.STOP_JOB,innMap);
		}
	}

	public void addManagerTaskForSimple(String triggerID, SimplePeriod period,boolean isDrools) throws SchedulerException {

		long now=System.currentTimeMillis();

		//the trigger had finished
		if(period.getEndTime()<now){
			return;
		}

		JobDataMap  innMap=new JobDataMap();
		innMap.put(ProxyJob.TRIGGER_ID,triggerID);


		if(period.getStartTime()>=now){
			Trigger triggerStart = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey(triggerID,START_PRE))
					.usingJobData(ProxyJob.TRIGGER_ID, triggerID)
					.usingJobData(ProxyJob.TYPE_SIGN,isDrools)
					.forJob(RuleEngScheduleFactory.START_JOB)
					.startAt(new Date(period.getStartTime()))
					.build();


			scheduler.scheduleJob(triggerStart);
		}else if(period.getEndTime()>=now){
			//fire miss start job by hand
			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB,innMap);
		}


		Trigger triggerEnd = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey(triggerID,STOP_PRE))
					.usingJobData(ProxyJob.TRIGGER_ID, triggerID)
					.usingJobData(ProxyJob.TYPE_SIGN,isDrools)
					.forJob(RuleEngScheduleFactory.STOP_JOB)
					.startAt(new Date(period.getEndTime()))
					.build();


		scheduler.scheduleJob(triggerEnd);
	}


	public void enableExecuteTask(String triggerID) throws SchedulerException {

		TriggerKey  key=TriggerKey.triggerKey(triggerID,EXEC_PRE);
		scheduler.resumeTrigger(key);

	}


	public void disableExecuteTask(String triggerID) throws SchedulerException {

		TriggerKey  key=TriggerKey.triggerKey(triggerID,EXEC_PRE);

		scheduler.pauseTrigger(key);

	}


	public void addExecuteTask(String triggerID, SchedulePrefix schedule,boolean enable)throws SchedulerException{

		TriggerKey key=TriggerKey.triggerKey(triggerID,EXEC_PRE);

		TriggerBuilder builder = TriggerBuilder
				.newTrigger()
				.withIdentity(key)
				.usingJobData(ProxyJob.TRIGGER_ID, triggerID)
				.forJob(RuleEngScheduleFactory.EXEC_JOB);


		if(schedule instanceof CronPrefix){
			builder.withSchedule(cronSchedule(((CronPrefix)schedule).getCron()).withMisfireHandlingInstructionDoNothing());


		}else if(schedule instanceof IntervalPrefix){
			IntervalPrefix interval=(IntervalPrefix)schedule;

			SimpleScheduleBuilder simple=simpleSchedule().withIntervalInMinutes(interval.getInterval());
			switch(interval.getTimeUnit()){
				case Hour:
					simple=simpleSchedule().withIntervalInHours(interval.getInterval());
					break;
				case Second:
					simple=simpleSchedule().withIntervalInSeconds(interval.getInterval());
					break;
				case Minute:
					simple=simpleSchedule().withIntervalInMinutes(interval.getInterval());
					break;
			}
			builder.withSchedule(simple.withMisfireHandlingInstructionNextWithExistingCount().repeatForever());
		}

		if(!enable){
			Calendar cal= Calendar.getInstance();
			cal.add(Calendar.MINUTE,1);
			builder.startAt(cal.getTime());
		}

		Trigger trigger=builder.build();

		scheduler.scheduleJob(trigger);

		if(!enable){
			scheduler.pauseTrigger(key);
		}


	}

	
	public void addManagerTask(String triggerID,TriggerValidPeriod period,boolean isDrools) throws SchedulerException {


		if(period instanceof SimplePeriod){
			addManagerTaskForSimple(triggerID,(SimplePeriod)period,isDrools);
		}else if(period instanceof SchedulePeriod){
			addManagerTaskForSchedule(triggerID,(SchedulePeriod)period,isDrools);
		}else{
			throw new IllegalArgumentException("invalid period type");
		}

	}

}
