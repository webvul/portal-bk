package com.kii.beehive.business.ruleengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.extension.ruleengine.EventCallback;

@Component
public class TriggerFireCallback implements EventCallback {


	@Autowired
	private BusinessEventBus eventBus;

	@Override
	public void onTriggerFire(String triggerID) {

		eventBus.onTriggerFire(triggerID);
	}
}
