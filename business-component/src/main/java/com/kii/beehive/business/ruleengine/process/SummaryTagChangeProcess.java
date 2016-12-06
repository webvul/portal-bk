package com.kii.beehive.business.ruleengine.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.groups.SummarySource;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component(BusinessEventListenerService.REFRESH_SUMMARY_GROUP)
public class SummaryTagChangeProcess implements TagChangeProcess {



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private BeehiveTriggerService engine;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Override
	public void onEventFire(EventListener listener) {

		String groupID= (String) listener.getCustoms().get(BusinessEventListenerService.GROUP_NAME);

		String triggerID=listener.getTargetKey();

		SummaryTriggerRecord record = (SummaryTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			SummarySource summary = record.getSummarySource().get(groupID);


			if (record == null || summary == null) {

				listenerService.disableTrigger(listener.getId());
				return;
			}

			engine.changeThingsInSummary(triggerID, groupID, thingTagService.getKiiThingIDs(summary.getSource()));
		}
	}
}