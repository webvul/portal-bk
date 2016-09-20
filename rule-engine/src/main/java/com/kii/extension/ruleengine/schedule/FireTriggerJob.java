package com.kii.extension.ruleengine.schedule;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.EventCallback;

@Component
public class FireTriggerJob implements JobInSpring {

	private Logger log= LoggerFactory.getLogger(StartTriggerJob.class);

	@Lazy
	@Autowired
	private ApplicationContext applicationCtx;


	public void execute(JobDataMap paramMap)  {

		String triggerID=paramMap.getString(ProxyJob.TRIGGER_ID);
		log.info("fire execute job trigger: "+triggerID);

		EventCallback callback=applicationCtx.getBean(EventCallback.class);

		Map<String,String> param=new HashMap<>();
		param.put("enable",Boolean.toString(true));

		callback.onTriggerFire(triggerID,param);


	}
}
