package com.kii.extension.ruleengine.schedule;

import com.kii.extension.ruleengine.RuleEngineConfig;
import com.kii.extension.ruleengine.store.trigger.SchedulePeriod;
import com.kii.extension.ruleengine.store.trigger.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Component
public class ScheduleService {
	
	
	public static final String TRIGGER_ID = "triggerID";
	private final Map<String,List<TriggerKey>> triggerKeysMap=new ConcurrentHashMap<>();
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

		GroupMatcher<TriggerKey> any= GroupMatcher.anyTriggerGroup();

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

	

	/**
	 * for reInit
	 */
	public void clearTrigger(){
		triggerKeysMap.values().forEach(triggerKeys -> {
			triggerKeys.forEach(triggerKey -> {
				try {
					scheduler.unscheduleJob(triggerKey);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});
		});
		triggerKeysMap.clear();
	}

	public void removeManagerTaskForSchedule(String triggerID) {
		List<TriggerKey> triggerKeys = triggerKeysMap.get(triggerID);
		triggerKeysMap.remove(triggerID);
		if(triggerKeys != null){
			triggerKeys.forEach(triggerKey -> {
				try {
					scheduler.unscheduleJob(triggerKey);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});
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

		triggerKeysMap.put(triggerID, Arrays.asList(triggerStart.getKey(), triggerEnd.getKey()));
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

		triggerKeysMap.put(triggerID, Arrays.asList(triggerStart.getKey(), triggerEnd.getKey()));
	}


	public void addManagerTask(String triggerID,TriggerValidPeriod predicate) throws SchedulerException {

		if(predicate==null){
			return;
		}
		if(predicate instanceof SimplePeriod){
			addManagerTaskForSimple(triggerID,(SimplePeriod)predicate);
		}else if(predicate instanceof SchedulePeriod){
			addManagerTaskForSchedule(triggerID,(SchedulePeriod)predicate);
		}else{
			throw new IllegalArgumentException("invalid period type");
		}
	}
}
