package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.ResultParam;

@Component
public class CommandExec {


	private Logger log= LoggerFactory.getLogger(CommandExec.class);

	@Autowired
	private EventCallback callback;



	private Map<String,AtomicInteger> map=new HashMap<>();


	public void doExecute(String triggerID,MatchResult result){

		int oldValue=map.computeIfAbsent(triggerID,(id)->new AtomicInteger(0)).incrementAndGet();
		log.info("execute trigger  " + triggerID+" been fire "+oldValue+" with params:"+result.getParams());

		callback.onTriggerFire(triggerID,result.getParams());

	}



	public void doExecute(String triggerID,ResultParam result){

		int oldValue=map.computeIfAbsent(triggerID,(id)->new AtomicInteger(0)).incrementAndGet();
		log.info("execute trigger  " + triggerID+" been fire "+oldValue+" with params:"+result.getParams());

		callback.onTriggerFire(triggerID,result.getParams());
	}


	public int getHitCount(String triggerID){

		return map.getOrDefault(triggerID,new AtomicInteger(0)).get();

	}

}
