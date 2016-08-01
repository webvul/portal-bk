package com.kii.extension.ruleengine.schedule;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleEngScheduleFactory {

	private static final String START = "start";
	private static final String STOP = "stop";
	private static final String EXECUTE_JOB = "ruleEngineTrigger";

	public static final JobKey START_JOB= JobKey.jobKey(START,EXECUTE_JOB);
	public static final JobKey STOP_JOB= JobKey.jobKey(STOP,EXECUTE_JOB);

	public static final String APPLICATION_CTX = "applicationCtx";
	public static final String BEAN_CLASS = "beanClass";


	@Autowired
	private StartTriggerJob startJob;

	@Autowired
	private StopTriggerJob stopJob;


	@Autowired
	private ApplicationContext applicationCtx;



	SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

	@Bean
	public Scheduler getScheduler() throws SchedulerException {

		Scheduler sched = schedFact.getScheduler();

		sched.addJob(getStartJob(),false);
		sched.addJob(getStopJob(),false);

		return sched;

	}

	private JobBuilder getJobBuilder(Class cls){

		JobDataMap dataMap=new JobDataMap();
		dataMap.put(APPLICATION_CTX,applicationCtx);
		dataMap.put(BEAN_CLASS,cls);

		return JobBuilder.newJob()
				.setJobData(dataMap)
				.storeDurably(true)
				.ofType(ProxyJob.class);


	}


	private JobDetail getStartJob(){

		return getJobBuilder(startJob.getClass())
				.withIdentity(START_JOB)
				.build();
	}

	private JobDetail  getStopJob(){

		return getJobBuilder(stopJob.getClass())
				.withIdentity(STOP_JOB)
				.build();
	}

}
