package com.kii.beehive.business.ruleengine.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.SummarySource;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;

@Component(BusinessEventListenerService.REFRESH_SUMMARY_GROUP)
public class SummaryTagChangeProcess implements TagChangeProcess {



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private EngineService engine;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Override
	public void onEventFire(EventListener listener) {

		String groupID= (String) listener.getCustoms().get(BusinessEventListenerService.GROUP_NAME);

		String triggerID=listener.getTargetKey();

		SummaryTriggerRecord record = (SummaryTriggerRecord) triggerDao.getEnableTriggerRecord(triggerID);


		SummarySource summary=record.getSummarySource().get(groupID);


		if(record==null||summary==null){

			listenerService.disableTrigger(listener.getId());
			return;
		}

		engine.changeThingsInSummary(triggerID,groupID,thingTagService.getKiiThingIDs(summary.getSource().getSelector()));
	}
}
