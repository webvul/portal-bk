package com.kii.beehive.business.event.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.business.manager.ThingGroupStateManager;
import com.kii.beehive.business.service.ThingTagService;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;

@Component(KiicloudEventListenerService.REFRESH_THING_GROUP)
@TagChanged
public class RefreshThingGroupProcess implements BeehiveEventProcess {


	@Autowired
	private ThingGroupStateManager triggerService;

//	@Autowired
//	private GlobalThingDao thingDao;

	@Autowired
	private TriggerRecordDao  triggerRecordDao;

	@Autowired
	private ThingTagService  thingService;

	@Override
	public void onEventFire(String triggerID, EventParam param) {


		GroupTriggerRecord record= (GroupTriggerRecord) triggerRecordDao.getObjectByID(triggerID);


		List<GlobalThingInfo>  things=thingService.getThingInfos(record.getSource().getSelector());


		triggerService.updateThingGroup(things,record);


	}
}
