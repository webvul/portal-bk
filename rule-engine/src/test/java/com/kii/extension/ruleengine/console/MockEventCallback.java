package com.kii.extension.ruleengine.console;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.ExecuteParam;

public class MockEventCallback implements EventCallback {


	private Logger log= LoggerFactory.getLogger(MockEventCallback.class);

	private Map<String,AtomicInteger>  count=new ConcurrentHashMap<>();


	@Override
	public void onTriggerFire(String triggerID,ExecuteParam params) {


		AtomicInteger  a=count.getOrDefault(triggerID,new AtomicInteger(0));


		log.info("the trigger "+triggerID+" been fired param:"+params+ "  count :"+a.incrementAndGet());

		count.put(triggerID,a);

	}
}
