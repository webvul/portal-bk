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
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.SummarySource;
import com.kii.beehive.portal.store.entity.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.EngineService;

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
		List<GlobalThingInfo> thingList=thingTagService.getThingInfos(summary.getSource().getSelector());
		Set<String> thingIDList=thingList.stream().map(thing->thing.getFullKiiThingID()).collect(Collectors.toSet());

		engine.changeThingsInSummary(triggerID,groupID,thingIDList);
	}
}
