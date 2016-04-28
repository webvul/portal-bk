package com.kii.beehive.portal.web.help;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExecutorThreadMaintain {


	@Autowired
	@Qualifier("myExecutor")
	private Executor springExecutor;


	public void doClear(){

		springExecutor.hashCode();

	}


}
