package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.drools.entity.CommResult;

@Component
public class CommandExec {


	private Logger log= LoggerFactory.getLogger(CommandExec.class);

	@Autowired
	private ApplicationContext applicationCtx;


	private Map<String,AtomicInteger> map=new HashMap<>();
	
	

	public void doExecute(String triggerID,CommResult result){

		int oldValue=map.computeIfAbsent(triggerID,(id)->new AtomicInteger(0)).incrementAndGet();


		log.info("execute trigger  " + triggerID+" been fire "+oldValue+" with params:"+result.getParams());

		if(!result.isEnable()){
			log.info("the trigger had been disabled ");
			return;
		}

		EventCallback  callback=applicationCtx.getBean(EventCallback.class);
		if(callback==null){
			log.error("not found valid callback instance ");
			return;
		}

		ExecuteParam  execParam=new ExecuteParam(result);
		
		try {
			
			callback.onTriggerFire(triggerID, execParam);
		}catch(Exception e){
			log.error("execute exec fail");
		}
	}




	public int getHitCount(String triggerID){

		return map.getOrDefault(triggerID,new AtomicInteger(0)).get();

	}



}
