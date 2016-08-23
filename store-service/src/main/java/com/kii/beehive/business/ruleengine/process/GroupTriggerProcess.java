package com.kii.beehive.business.ruleengine.process;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component(BusinessEventListenerService.REFRESH_THING_GROUP)
public class GroupTriggerProcess implements TagChangeProcess {


	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private TriggerRecordDao triggerRecordDao;



	@Autowired
	private BusinessEventListenerService listenerService;

	@Autowired
	private EngineService engine;


	@Override
	public void onEventFire(EventListener  listener) {


		String listenerID=listener.getId();

		GroupTriggerRecord trigger= (GroupTriggerRecord) triggerRecordDao.getTriggerRecord(listener.getTargetKey());

		if(trigger==null){
			listenerService.disableTrigger(listenerID);
			return;
		}

		if(trigger.getRecordStatus()== TriggerRecord.StatusType.enable) {

			Set<String> thingIDList = thingTagService.getKiiThingIDs(trigger.getSource());
			engine.changeThingsInTrigger(trigger.getId(), thingIDList);
		}
	}
}
