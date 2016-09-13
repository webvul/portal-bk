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
public class StopTriggerJob implements JobInSpring {

	private Logger log= LoggerFactory.getLogger(StopTriggerJob.class);

	@Lazy
	@Autowired
	private DroolsTriggerService bean;



	@Override
	public void execute(JobDataMap paramMap) {
		String triggerID=paramMap.getString(ProxyJob.TRIGGER_ID);

		boolean isDrools=paramMap.getBoolean(ProxyJob.TYPE_SIGN);

		if(isDrools) {
			log.info("stop job disable trigger: " + triggerID);

			bean.disableTrigger(triggerID);
		}else{

			JobExecutionContext context=(JobExecutionContext)paramMap.get(ProxyJob.JOB_CONTEXT);
			try {
				context.getScheduler().pauseTrigger(TriggerKey.triggerKey(triggerID,ScheduleService.EXEC_PRE));
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
}
