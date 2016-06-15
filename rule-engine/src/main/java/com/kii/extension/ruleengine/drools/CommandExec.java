package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.ResultParam;

@Component
public class CommandExec {


	private Logger log= LoggerFactory.getLogger(CommandExec.class);

	@Autowired
	private EventCallback callback;


	private ScheduledExecutorService  executeService=new ScheduledThreadPoolExecutor(10);

	private Map<String,AtomicInteger> map=new HashMap<>();


	public void doExecute(String triggerID,MatchResult result){

		int oldValue=map.computeIfAbsent(triggerID,(id)->new AtomicInteger(0)).incrementAndGet();
		log.info("execute trigger  " + triggerID+" been fire "+oldValue+" with params:"+result.getParams());

		if(StringUtils.isEmpty(result.getDelay())) {

			callback.onTriggerFire(triggerID, result.getParams());

		}else{
			Callable<Integer> call=new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					callback.onTriggerFire(triggerID,result.getParams());
					return 0;
				}
			};
			int delay=Integer.parseInt(result.getDelay());
			executeService.schedule(call,delay, TimeUnit.MINUTES );

		}

	}



	public void doExecute(String triggerID,ResultParam result){

		int oldValue=map.computeIfAbsent(triggerID,(id)->new AtomicInteger(0)).incrementAndGet();
		log.info("execute trigger  " + triggerID+" been fire "+oldValue+" with params:"+result.getParams());

		if(StringUtils.isEmpty(result.getDelay())) {

			callback.onTriggerFire(triggerID, result.getParams());

		}else{
			Callable<Integer> call=new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					callback.onTriggerFire(triggerID,result.getParams());
					return 0;
				}
			};
			int delay=Integer.parseInt(result.getDelay());

			executeService.schedule(call,delay, TimeUnit.MINUTES );

		}

	}


	public int getHitCount(String triggerID){

		return map.getOrDefault(triggerID,new AtomicInteger(0)).get();

	}

}
