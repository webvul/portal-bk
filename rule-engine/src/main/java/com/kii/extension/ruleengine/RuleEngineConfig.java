package com.kii.extension.ruleengine;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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

}
