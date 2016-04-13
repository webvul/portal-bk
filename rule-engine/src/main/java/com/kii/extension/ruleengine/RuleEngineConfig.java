package com.kii.extension.ruleengine;

import org.quartz.JobKey;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.kii.extension.ruleengine")
public class RuleEngineConfig {

	private static final String START = "start";
	private static final String STOP = "stop";
	private static final String EXECUTE_JOB = "execute";

	public static final JobKey START_JOB= JobKey.jobKey(START,EXECUTE_JOB);
	public static final JobKey STOP_JOB= JobKey.jobKey(STOP,EXECUTE_JOB);

	public static final String APPLICATION_CTX = "applicationCtx";
	public static final String BEAN_CLASS = "beanClass";

}
