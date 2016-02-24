package com.kii.extension.ruleengine.schedule;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecuteJob implements JobInSpring {

	@Autowired
	private  BusinessBean bean;

	@Override
	public void execute(JobDataMap paramMap) {

		bean.output("execute "+paramMap.getString("triggerID"));
	}
}
