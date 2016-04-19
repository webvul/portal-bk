package com.kii.beehive.business.ruleengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component
public class TriggerFireCallback implements EventCallback {


	private Logger log= LoggerFactory.getLogger(TriggerFireCallback.class);

	@Autowired
	private CommandExecuteService execService;

	@Autowired
	private TriggerLogTools logTools;


	@Autowired
	private TriggerRecordDao triggerRecordDao;

	@Override
	public void onTriggerFire(String triggerID) {

		TriggerRecord trigger=triggerRecordDao.getEnableTriggerRecord(triggerID);

		if(trigger==null){
			log.error("the trigger not been found :"+triggerID);
			return;
		}

		execService.doCommand(trigger);

		logTools.outputFireLog(triggerID);

	}
}
