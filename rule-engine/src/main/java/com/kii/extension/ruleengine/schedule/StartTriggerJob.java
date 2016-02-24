package com.kii.extension.ruleengine.schedule;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartTriggerJob implements JobInSpring {

	@Autowired
	private  BusinessBean bean;

	public void execute(JobDataMap paramMap)  {
		bean.output("start "+paramMap.getString("triggerID"));


	}
}
