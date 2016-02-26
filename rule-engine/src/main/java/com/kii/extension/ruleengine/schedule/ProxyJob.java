package com.kii.extension.ruleengine.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class ProxyJob implements Job {

	private Class<? extends JobInSpring> beanClass;

	private ApplicationContext applicationCtx;

	public Class getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}

	public ApplicationContext getApplicationCtx() {
		return applicationCtx;
	}

	public void setApplicationCtx(ApplicationContext applicationCtx) {
		this.applicationCtx = applicationCtx;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap=context.getMergedJobDataMap();

		JobInSpring  jobInSpring=applicationCtx.getBean(beanClass);

		jobInSpring.execute(dataMap);

	}
}
