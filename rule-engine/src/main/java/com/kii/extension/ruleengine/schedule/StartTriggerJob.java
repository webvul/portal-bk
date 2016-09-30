package com.kii.extension.ruleengine.schedule;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;

@Component
public class StartTriggerJob implements JobInSpring {

	private Logger log= LoggerFactory.getLogger(StartTriggerJob.class);

	@Lazy
	@Autowired
	private DroolsTriggerService bean;

	public void execute(JobDataMap paramMap)  {

		String triggerID=paramMap.getString(ProxyJob.TRIGGER_ID);

		log.info("stop job disable trigger: " + triggerID);


		JobExecutionContext context=(JobExecutionContext)paramMap.get(ProxyJob.JOB_CONTEXT);
		try {
				context.getScheduler().resumeTrigger(TriggerKey.triggerKey(triggerID,ScheduleService.EXEC_PRE));
		} catch (SchedulerException e) {
				e.printStackTrace();
		}

		bean.enableTrigger(triggerID);

	}

}
