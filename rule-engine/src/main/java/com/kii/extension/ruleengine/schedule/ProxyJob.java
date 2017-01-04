package com.kii.extension.ruleengine.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class ProxyJob implements Job {


//	public static final String TYPE_SIGN = "Type_Sign";
	public static final String JOB_CONTEXT = "Job_Context";
	public static final String TRIGGER_ID = "triggerID";
	public static final String WITH_TIMER="Is_Timer";
	
	
	
	public static final String APPLICATION_CTX = "applicationCtx";
	public static final String BEAN_CLASS = "beanClass";
	
	
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

		dataMap.put(JOB_CONTEXT,context);

		JobInSpring  jobInSpring=applicationCtx.getBean(beanClass);

		jobInSpring.execute(dataMap);



	}


}
