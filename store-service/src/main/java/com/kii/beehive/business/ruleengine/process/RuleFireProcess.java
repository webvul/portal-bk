package com.kii.beehive.business.ruleengine.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TriggerFireProcess;
import com.kii.beehive.business.ruleengine.CommandExecuteService;
import com.kii.beehive.business.ruleengine.TriggerLogTools;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component(BusinessEventListenerService.FIRE_TRIGGER_WHEN_MATCH)
public class RuleFireProcess implements TriggerFireProcess {


	@Autowired
	private CommandExecuteService execService;

	@Autowired
	private TriggerRecordDao triggerRecordDao;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Autowired
	private TriggerLogTools logTools;


	@Override
	public void onEventFire(EventListener listener, String triggerID) {

		TriggerRecord trigger=triggerRecordDao.getEnableTriggerRecord(triggerID);

		if(trigger==null){

			listenerService.disableTrigger(listener.getId());

			return;
		}

		execService.doCommand(trigger);

		logTools.outputLog(triggerID);

	}
}
