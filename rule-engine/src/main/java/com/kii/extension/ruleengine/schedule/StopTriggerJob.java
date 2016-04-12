package com.kii.extension.ruleengine.schedule;

import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;

@Component
public class StopTriggerJob implements JobInSpring {

	private Logger log= LoggerFactory.getLogger(StartTriggerJob.class);

	@Autowired
	private DroolsTriggerService bean;

	@Override
	public void execute(JobDataMap paramMap) {
		String triggerID=paramMap.getString(ScheduleService.TRIGGER_ID);
		log.info("stop job disable trigger: "+triggerID);

		bean.disableTrigger(triggerID);

	}
}
