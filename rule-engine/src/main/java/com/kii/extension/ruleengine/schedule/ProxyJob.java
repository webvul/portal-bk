package com.kii.extension.ruleengine.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class ProxyJob implements Job {

	private String beanName;

	private ApplicationContext appplicationCtx;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public ApplicationContext getAppplicationCtx() {
		return appplicationCtx;
	}

	public void setAppplicationCtx(ApplicationContext appplicationCtx) {
		this.appplicationCtx = appplicationCtx;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap=context.getMergedJobDataMap();

		JobInSpring  jobInSpring=appplicationCtx.getBean(beanName,JobInSpring.class);

		jobInSpring.execute(dataMap);

	}
}
