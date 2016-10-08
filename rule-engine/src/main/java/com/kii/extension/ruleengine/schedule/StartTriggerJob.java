package com.kii.extension.ruleengine.schedule;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
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

	@Lazy
	@Autowired
	private TriggerFactory  factory;


	public void execute(JobDataMap paramMap)  {

		String triggerID=paramMap.getString(ProxyJob.TRIGGER_ID);

		log.info("start job enable trigger: " + triggerID);

		JobExecutionContext context=(JobExecutionContext)paramMap.get(ProxyJob.JOB_CONTEXT);

		String   prefix= paramMap.getString(ProxyJob.WITH_TIMER);

		if(prefix!=null) {
			try {
				Trigger trigger = factory.getExecuteTask(triggerID, prefix);
				context.getScheduler().scheduleJob(trigger);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		bean.enableTrigger(triggerID);

	}

}
