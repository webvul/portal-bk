package com.kii.extension.ruleengine.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.EventCallback;

public class MockEventCallback implements EventCallback {


	private Logger log= LoggerFactory.getLogger(MockEventCallback.class);

	@Override
	public void onTriggerFire(String triggerID) {


		log.info("the trigger "+triggerID+" been fired");

	}
}
