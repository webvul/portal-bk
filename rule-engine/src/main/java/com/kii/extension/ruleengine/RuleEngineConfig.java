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


	@Autowired
	private StartTriggerJob  startJob;

	@Autowired
	private StopTriggerJob  stopJob;

	@Autowired
	private ExecuteJob  execJob;

	@Autowired
	private ApplicationContext  applicationCtx;

	@Bean
	public Scheduler getScheduler() throws SchedulerException {

		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		Scheduler sched = schedFact.getScheduler();


		sched.addJob(getExeJob(),false);
		sched.addJob(getStartJob(),false);
		sched.addJob(getStopJob(),false);

		return sched;

	}

	private JobDetail  getExeJob(){


		JobDataMap  dataMap=new JobDataMap();
		dataMap.put("applicationCtx",applicationCtx);
		dataMap.put("beanClass",execJob.getClass());

		return JobBuilder.newJob()
				.withIdentity("execute","exe")
				.setJobData(dataMap)
				.ofType(ProxyJob.class)
				.build();
	}


	private JobDetail  getStartJob(){


		JobDataMap  dataMap=new JobDataMap();
		dataMap.put("applicationCtx",applicationCtx);
		dataMap.put("beanClass",startJob.getClass());

		return JobBuilder.newJob()
				.withIdentity("start","manager")
				.setJobData(dataMap)
				.ofType(ProxyJob.class)
				.build();
	}

	private JobDetail  getStopJob(){


		JobDataMap  dataMap=new JobDataMap();
		dataMap.put("applicationCtx",applicationCtx);
		dataMap.put("beanClass",stopJob.getClass());

		return JobBuilder.newJob()
				.withIdentity("stop","manager")
				.setJobData(dataMap)
				.ofType(ProxyJob.class)
				.build();
	}


}
