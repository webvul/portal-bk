package com.kii.beehive.business.ruleengine.process;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.event.impl.TagChangeProcess;
import com.kii.beehive.business.manager.ThingStateManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.BusinessTriggerDao;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.extension.ruleengine.EngineService;

@Component(BusinessEventListenerService.REFRESH_THING_GROUP)
public class GroupTriggerProcess implements TagChangeProcess {


	@Autowired
	private ThingStateManager thingTagService;

	@Autowired
	private TriggerRecordDao triggerRecordDao;

	@Autowired
	private BusinessTriggerDao triggerDao;

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

		TagSelector  selector=trigger.getSource().getSelector();

		List<GlobalThingInfo> thingList=thingTagService.getThingInfos(selector);
		Set<String> thingIDList=thingList.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toSet());
		engine.changeThingsInTrigger(trigger.getId(),thingIDList);
	}
}
