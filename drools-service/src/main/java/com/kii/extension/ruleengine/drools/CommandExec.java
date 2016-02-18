package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class CommandExec {


	private Map<Integer,AtomicInteger> map=new HashMap<>();


	public void doExecute(int triggerID){

		int oldValue=map.computeIfAbsent(triggerID,(id)->new AtomicInteger(0)).incrementAndGet();
		System.out.println("execute trigger  " + triggerID+" been fire "+oldValue);
	}

	public int getHitCount(int triggerID){

		return map.getOrDefault(triggerID,new AtomicInteger(0)).get();

	}

}
