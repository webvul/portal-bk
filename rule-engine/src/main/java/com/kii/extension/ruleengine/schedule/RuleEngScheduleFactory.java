package com.kii.extension.ruleengine.schedule;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kii.extension.ruleengine.RuleEngineConfig;

@Configuration
public class RuleEngScheduleFactory {



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
		dataMap.put(RuleEngineConfig.APPLICATION_CTX,applicationCtx);
		dataMap.put(RuleEngineConfig.BEAN_CLASS,cls);

		return JobBuilder.newJob()
				.setJobData(dataMap)
				.storeDurably(true)
				.ofType(ProxyJob.class);


	}


	private JobDetail getStartJob(){

		return getJobBuilder(startJob.getClass())
				.withIdentity(RuleEngineConfig.START_JOB)
				.build();
	}

	private JobDetail  getStopJob(){

		return getJobBuilder(stopJob.getClass())
				.withIdentity(RuleEngineConfig.STOP_JOB)
				.build();
	}

}
