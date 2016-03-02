package com.kii.extension.schedule;

import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.drools.DroolsTriggerService;

@Component
public class StartTriggerJob implements JobInSpring {

	private Logger log= LoggerFactory.getLogger(StartTriggerJob.class);

	@Autowired
	private DroolsTriggerService bean;

	public void execute(JobDataMap paramMap)  {

		String triggerID=paramMap.getString(ScheduleService.TRIGGER_ID);
		log.info("start "+triggerID);

		bean.enableTrigger(triggerID);


	}
}
