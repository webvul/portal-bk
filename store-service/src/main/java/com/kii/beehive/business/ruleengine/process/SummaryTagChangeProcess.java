package com.kii.beehive.business.ruleengine.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.manager.ThingStateManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.service.TriggerRecordDao;
import com.kii.extension.store.trigger.SummarySource;
import com.kii.extension.store.trigger.SummaryTriggerRecord;
import com.kii.extension.EngineService;

@Component(BusinessEventListenerService.REFRESH_SUMMARY_GROUP)
public class SummaryTagChangeProcess implements TagChangeProcess {



	@Autowired
	private ThingStateManager thingTagService;


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

		SummaryTriggerRecord record = (SummaryTriggerRecord) triggerDao.getTriggerRecord(triggerID);


		SummarySource summary=record.getSummarySource().get(groupID);


		if(record==null||summary==null){

			listenerService.disableTrigger(listener.getId());
			return;
		}

		engine.changeThingsInSummary(triggerID,groupID,thingTagService.getKiiThingIDs(summary.getSource().getSelector()));
	}
}
