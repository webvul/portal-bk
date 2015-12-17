package com.kii.beehive.business.manager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.service.GroupStateCallbackService;
import com.kii.beehive.business.service.KiiTriggerRegistService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;

@Component
public class BeehiveTriggerManager {


	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private KiiTriggerRegistService  registService;

	@Autowired
	private GroupStateCallbackService groupService;

	@Autowired
	private GlobalThingDao thingDao;


	public String  createTrigger(TriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();


		TriggerSource source=record.getSource();

		if(source.get)


		return triggerID;
	}

}
