package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.business.manager.ThingStateSummaryManager;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRuntimeState;

@Component(BusinessEventListenerService.REFRESH_SUMMARY_GROUP)
public class SummaryTagChangeProcess implements TagChangeProcess {


	@Autowired
	private ThingStateSummaryManager summaryService;

	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private TriggerRuntimeStatusDao stateDao;

	@Autowired
	private BusinessEventListenerService listenerService;

	@Override
	public void onEventFire(EventListener listener) {

		String groupID= (String) listener.getCustoms().get(BusinessEventListenerService.GROUP_NAME);

		String triggerID=listener.getTargetKey();

		SummaryTriggerRecord record = (SummaryTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		SummaryTriggerRuntimeState state=stateDao.getSummaryRuntimeState(triggerID);

		if(record==null||state==null){

			listenerService.disableTrigger(listener.getId());
			return;
		}

		summaryService.onTagChanged(record,state,groupID);
	}
}
