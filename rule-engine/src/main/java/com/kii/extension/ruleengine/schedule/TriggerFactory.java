package com.kii.extension.ruleengine.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.store.entity.trigger.schedule.CronPrefix;
import com.kii.beehive.portal.store.entity.trigger.schedule.IntervalPrefix;
import com.kii.beehive.portal.store.entity.trigger.schedule.SchedulePeriod;
import com.kii.beehive.portal.store.entity.trigger.schedule.SchedulePrefix;
import com.kii.beehive.portal.store.entity.trigger.schedule.SimplePeriod;
import com.kii.beehive.portal.store.entity.trigger.schedule.TriggerValidPeriod;

@Component
public class TriggerFactory {


	private static final String STOP_PRE = "Stop";
	private static final String START_PRE = "Start";

	private static final String EXEC_PRE="Exec";

	@Autowired
	private ObjectMapper mapper;


//	private Map<String,SchedulePrefix> schedulePrefixMap=new ConcurrentHashMap<>();


	public static  TriggerKey getStartTriggerKey(String triggerID){
		return TriggerKey.triggerKey(triggerID,START_PRE);
	}


	public static TriggerKey getStopTriggerKey(String triggerID){
		return TriggerKey.triggerKey(triggerID,STOP_PRE);
	}


	public static TriggerKey getExecTriggerKey(String triggerID){
		return TriggerKey.triggerKey(triggerID,EXEC_PRE);
	}

	public static List<TriggerKey> getAllTriggers(String triggerID){

		List<TriggerKey> list=new ArrayList<>();

		list.add(getStartTriggerKey(triggerID));
		list.add(getStopTriggerKey(triggerID));
		list.add(getExecTriggerKey(triggerID));


		return list;

	}

	public  Trigger getExecuteTask(String triggerID,String scheduleJson) throws SchedulerException, IOException {


		SchedulePrefix schedule = null;
		try {
			schedule = mapper.readValue(scheduleJson, SchedulePrefix.class);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		return getExecuteTask(triggerID,schedule);
	}


	public  Trigger getExecuteTask(String triggerID,SchedulePrefix  schedule) throws SchedulerException, IOException {


		TriggerKey key=getExecTriggerKey(triggerID);

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

		Trigger trigger=builder.build();

		return trigger;


	}


	public  Trigger[] addManagerTask(String triggerID, TriggerValidPeriod period,SchedulePrefix schedule) throws SchedulerException, JsonProcessingException {

		String json=mapper.writeValueAsString(schedule);

		if(period instanceof SimplePeriod){
			return addManagerTaskForSimple(triggerID,(SimplePeriod)period,json);
		}else if(period instanceof SchedulePeriod){
			return addManagerTaskForSchedule(triggerID,(SchedulePeriod)period,json);
		}else{
			throw new IllegalArgumentException("invalid period type");
		}

	}

	public   Trigger[] addManagerTaskForSchedule(String triggerID, SchedulePeriod period,String schedule) throws SchedulerException {


		Trigger[] array=new Trigger[2];


		Trigger triggerStart= TriggerBuilder.newTrigger()
				.withIdentity(getStartTriggerKey(triggerID))
				.usingJobData(ProxyJob.TRIGGER_ID,triggerID)
				.usingJobData(ProxyJob.WITH_TIMER,schedule)
				.forJob(RuleEngScheduleFactory.START_JOB)
				.withSchedule(cronSchedule(period.getStartCron()))
				.build();


		array[0]=triggerStart;


		Trigger triggerEnd= TriggerBuilder.newTrigger()
				.withIdentity(getStopTriggerKey(triggerID))
				.usingJobData(ProxyJob.TRIGGER_ID,triggerID)
				.usingJobData(ProxyJob.WITH_TIMER,schedule)
				.forJob(RuleEngScheduleFactory.STOP_JOB)
				.withSchedule(cronSchedule(period.getEndCron()))
				.build();

		array[1]=triggerEnd;

		return array;

//		scheduler.scheduleJob(triggerEnd);
//
//		Date nextStop=triggerEnd.getNextFireTime();
//		Date nextStart=triggerStart.getNextFireTime();
//
//		JobDataMap innMap=new JobDataMap();
//		innMap.put(ProxyJob.TRIGGER_ID,triggerID);
//
//		if(nextStart.getTime()>=nextStop.getTime() ){
//
//			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB,innMap);
//		}else{
//			scheduler.triggerJob(RuleEngScheduleFactory.STOP_JOB,innMap);
//		}
	}

	public Trigger[] addManagerTaskForSimple(String triggerID, SimplePeriod period,String schedule) throws SchedulerException {


		Trigger[] array=new Trigger[2];


		long now=System.currentTimeMillis();

		//the trigger had finished
		if(period.getEndTime()<now){
			return null;
		}

		JobDataMap  innMap=new JobDataMap();
		innMap.put(ProxyJob.TRIGGER_ID,triggerID);


		if(period.getStartTime()>=now){
			Trigger triggerStart = TriggerBuilder.newTrigger()
					.withIdentity(getStartTriggerKey(triggerID))
					.usingJobData(ProxyJob.TRIGGER_ID, triggerID)
					.usingJobData(ProxyJob.WITH_TIMER,schedule)
					.forJob(RuleEngScheduleFactory.START_JOB)
					.startAt(new Date(period.getStartTime()))
					.build();

			array[0]=triggerStart;
		}
//		else if(period.getEndTime()>=now){
//			//fire miss start job by hand
//			scheduler.triggerJob(RuleEngScheduleFactory.START_JOB,innMap);
//		}


		Trigger triggerEnd = TriggerBuilder.newTrigger()
				.withIdentity(getStopTriggerKey(triggerID))
				.usingJobData(ProxyJob.TRIGGER_ID, triggerID)
				.usingJobData(ProxyJob.WITH_TIMER,schedule)
				.forJob(RuleEngScheduleFactory.STOP_JOB)
				.startAt(new Date(period.getEndTime()))
				.build();


		array[1]=triggerEnd;
//		scheduler.scheduleJob(triggerEnd);

		return array;
	}


}
