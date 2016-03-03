package com.kii.extension.ruleengine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.drools.DroolsRuleService;
import com.kii.extension.ruleengine.drools.CommandExec;
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
	private StartTriggerJob startJob;

	@Autowired
	private StopTriggerJob stopJob;


	@Autowired
	private ApplicationContext  applicationCtx;


	@Autowired
	private CommandExec exec;


	@Autowired
	protected ResourceLoader loader;



	private String getDrlContent(String fileName) {

		try {
			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}


	@Bean(name="cloudDroolsService")
	public DroolsRuleService getCloudService(){

		DroolsRuleService droolsService= new DroolsRuleService(false,
				getDrlContent("triggerComm"),
				getDrlContent("groupPolicy"),
				getDrlContent("summaryCompute"));

		droolsService.bindWithInstance("exec",exec);

		return droolsService;

	}

	@Bean(name="streamDroolsService")
	public DroolsRuleService  getStreamService(){

		DroolsRuleService droolsService= new DroolsRuleService(true,
				getDrlContent("triggerComm"),
				getDrlContent("groupPolicy"));

		droolsService.bindWithInstance("exec",exec);

		return droolsService;

	}




	SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

	@Bean
	public Scheduler getScheduler() throws SchedulerException {

		Scheduler sched = schedFact.getScheduler();

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
