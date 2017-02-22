package com.kii.extension.ruleengine.schedule;

import javax.annotation.PreDestroy;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.store.entity.trigger.schedule.SchedulePrefix;
import com.kii.beehive.portal.store.entity.trigger.schedule.TriggerValidPeriod;

@Component
public class ScheduleService {
	

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private TriggerFactory factory;

	@Autowired
	private ObjectMapper mapper;

	@PreDestroy
	public void stop() {
		
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);

		}
	}


	public Map<String, Object> dump(String triggerID) {


		Set<Object> triggerMap = new HashSet<>();

		try {

			Set<TriggerKey> keys = new HashSet<>();

			if (triggerID == null) {
				GroupMatcher<TriggerKey> any = GroupMatcher.anyGroup();

				keys = scheduler.getTriggerKeys(any);
			} else {
				keys.addAll(TriggerFactory.getAllTriggers(triggerID));
			}

			keys.forEach((k) -> {
				try {
					Trigger trigger = scheduler.getTrigger(k);
					if (trigger == null) {
						return;
					}
					Map<String, Object> data = new HashMap<>();
					data.put("endTime", trigger.getEndTime());
					data.put("startTime", trigger.getStartTime());

					data.put("nextFireTime", trigger.getNextFireTime());
					data.put("previousFireTime", trigger.getPreviousFireTime());

					data.put("jobType", k.getGroup());

					triggerMap.add(data);


				} catch (SchedulerException e) {
					e.printStackTrace();
				}

			});
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return Collections.singletonMap("schedule", triggerMap);
	}


	/**
	 * for reInit
	 */
	public void clearTrigger() {

		try {
			Set<TriggerKey> triggers = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());

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
			scheduler.unscheduleJobs(TriggerFactory.getAllTriggers(triggerID));

		} catch (SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
	}


	public void enableExecuteTask(String triggerID) throws SchedulerException {

		scheduler.resumeTrigger(TriggerFactory.getExecTriggerKey(triggerID));

	}


	public void disableExecuteTask(String triggerID) throws SchedulerException {


		scheduler.pauseTrigger(TriggerFactory.getExecTriggerKey(triggerID));

	}


	public void addManagerTask(String triggerID, TriggerValidPeriod period, SchedulePrefix schedule) throws SchedulerException, IOException {


		if(period==null){
			addExecTask(triggerID,schedule);
			return;
		}

		Trigger[] array = factory.addManagerTask(triggerID, period, schedule);

		if (array == null) {
			return;
		}

		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				scheduler.scheduleJob(array[i]);
			}
		}

		JobDataMap innMap = new JobDataMap();
		innMap.put(ProxyJob.TRIGGER_ID, triggerID);
		innMap.put(ProxyJob.WITH_TIMER,mapper.writeValueAsString(schedule));


		if (array[0] == null) {
			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB, innMap);
			return;
		}

		if (array[1] != null && array[0] != null) {

			Date nextStart = array[0].getNextFireTime();
			Date nextStop = array[1].getNextFireTime();

			if (nextStart.getTime() >= nextStop.getTime()) {

				scheduler.triggerJob(RuleEngScheduleFactory.START_JOB, innMap);
			} else {
				scheduler.triggerJob(RuleEngScheduleFactory.STOP_JOB, innMap);
			}
		}


	}

	private void addExecTask(String triggerID, SchedulePrefix schedule) throws SchedulerException, IOException {

		if(schedule==null){
			return;
		}

		Trigger trigger=factory.getExecuteTask(triggerID,schedule);

		scheduler.scheduleJob(trigger);

	}
}