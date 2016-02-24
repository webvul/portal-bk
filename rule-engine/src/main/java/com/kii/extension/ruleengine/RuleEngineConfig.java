package com.kii.extension.ruleengine;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.kii.extension.ruleengine.schedule.ExecuteJob;
import com.kii.extension.ruleengine.schedule.ProxyJob;
import com.kii.extension.ruleengine.schedule.StartTriggerJob;
import com.kii.extension.ruleengine.schedule.StopTriggerJob;

@Configuration
@ComponentScan("com.kii.extension.ruleengine")
public class RuleEngineConfig {


	public static final String MANAGER_GROUP = "manager";
	public static final String EXE_GROUP = "exe";

	public static final String START_JOB = "start";
	public static final String STOP_JOB = "stop";
	public static final String EXECUTE_JOB = "execute";

	public static final String APPLICATION_CTX = "applicationCtx";
	public static final String BEAN_CLASS = "beanClass";

	@Autowired
	private StartTriggerJob  startJob;

	@Autowired
	private StopTriggerJob  stopJob;

	@Autowired
	private ExecuteJob  execJob;

	@Autowired
	private ApplicationContext  applicationCtx;

	SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

	@Bean
	public Scheduler getScheduler() throws SchedulerException {

		Scheduler sched = schedFact.getScheduler();

		sched.addJob(getExeJob(),false);
		sched.addJob(getStartJob(),false);
		sched.addJob(getStopJob(),false);

		return sched;

	}

	private JobBuilder getJobBuilder(Class cls){

		JobDataMap  dataMap=new JobDataMap();
		dataMap.put(APPLICATION_CTX,applicationCtx);
		dataMap.put(BEAN_CLASS,cls);

		return JobBuilder.newJob()
				.setJobData(dataMap)
				.storeDurably(true)
				.ofType(ProxyJob.class);


	}

	private JobDetail  getExeJob(){

		return getJobBuilder(execJob.getClass())
				.withIdentity(EXECUTE_JOB, EXE_GROUP)
				.build();
	}


	private JobDetail  getStartJob(){

		return getJobBuilder(startJob.getClass())
				.withIdentity(START_JOB, MANAGER_GROUP)
				.build();
	}

	private JobDetail  getStopJob(){

		return getJobBuilder(stopJob.getClass())
				.withIdentity(STOP_JOB,MANAGER_GROUP)
				.build();
	}


}
