package com.kii.extension.ruleengine.schedule;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopTriggerJob implements JobInSpring {

	@Autowired
	private  BusinessBean bean;

	@Override
	public void execute(JobDataMap paramMap) {
		bean.output("end trigger "+paramMap.getString(ScheduleService.TRIGGER_ID));

	}
}
