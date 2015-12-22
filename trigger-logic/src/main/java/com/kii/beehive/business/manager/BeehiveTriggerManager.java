package com.kii.beehive.business.manager;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.service.GroupStateCallbackService;
import com.kii.beehive.business.service.KiiTriggerRegistService;
import com.kii.beehive.business.service.ThingGroupStateService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;

@Component
@Transactional
public class BeehiveTriggerManager {


	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private KiiTriggerRegistService  registService;

	@Autowired
	private ThingGroupStateService groupService;

	@Autowired
	private GlobalThingDao thingDao;


	public String  createTrigger(TriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();


		TriggerSource source=record.getSource();

		if(source.getThingList().size()==1) {
			GlobalThingInfo thing=thingDao.getThingByVendorThingID(source.getThingList().iterator().next());
			registService.registSingleTrigger(thing.getKiiThingID(),record.getPerdicate(),triggerID);

		}else {
			List<GlobalThingInfo> things = thingDao.getThingsByIDArray(source.getThingList());

			things.forEach(thing->{
				registService.registDoubleTrigger(thing.getKiiThingID(),record.getPerdicate().getCondition(),triggerID);
			});

			groupService.createThingGroup(things,record.getPerdicate().getTriggersWhen(),triggerID,source);
		}

		return triggerID;
	}

}
