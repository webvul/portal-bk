package com.kii.beehive.business.ruleEngine;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TriggerFireProcess;
import com.kii.beehive.business.ruleengine.CommandExecuteService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

//@Component
public class MockRuleFireProcess implements TriggerFireProcess{

	@Autowired
	private CommandExecuteService execService;

	@Autowired
	private TriggerRecordDao triggerRecordDao;

	@Autowired
	private BusinessEventListenerService listenerService;


	@Override
	public void onEventFire(EventListener listener, String triggerID) {

		TriggerRecord trigger=triggerRecordDao.getEnableTriggerRecord(triggerID);

		if(trigger==null){

			listenerService.disableTrigger(listener.getId());

			return;
		}



	}
}
