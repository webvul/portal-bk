package com.kii.beehive.business.ruleEngine.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.ExecuteParam;

public class MockEventCallback implements EventCallback {


	private Logger log= LoggerFactory.getLogger(MockEventCallback.class);

	@Override
	public void onTriggerFire(String triggerID,ExecuteParam map) {


		log.info("the trigger "+triggerID+" been fired");

	}
}
