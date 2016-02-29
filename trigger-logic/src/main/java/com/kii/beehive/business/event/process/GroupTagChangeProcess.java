package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.business.manager.ThingGroupStateManager;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.service.TriggerRuntimeStatusDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRuntimeState;

@Component(BusinessEventListenerService.REFRESH_THING_GROUP)
public class GroupTagChangeProcess implements TagChangeProcess {


	@Autowired
	private ThingGroupStateManager triggerService;


	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private TriggerRuntimeStatusDao stateDao;

	@Autowired
	private BusinessEventListenerService listenerService;



	@Override
	public void onEventFire(EventListener listener) {

		String triggerID=listener.getTargetKey();

		GroupTriggerRecord record= (GroupTriggerRecord) triggerDao.getTriggerRecord(triggerID);

		GroupTriggerRuntimeState state=stateDao.getGroupRuntimeState(triggerID);

		if(record==null||state==null){

			this.listenerService.disableTrigger(listener.getId());
			return;
		}

		triggerService.onTagChanged(record,state);


	}
}
