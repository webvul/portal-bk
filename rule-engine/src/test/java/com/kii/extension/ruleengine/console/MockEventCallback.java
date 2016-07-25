package com.kii.extension.ruleengine.console;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.EventCallback;

public class MockEventCallback implements EventCallback {


	private Logger log= LoggerFactory.getLogger(MockEventCallback.class);

	@Override
	public void onTriggerFire(String triggerID,Map<String,String> params) {


		log.info("the trigger "+triggerID+" been fired param:"+params);



	}
}
