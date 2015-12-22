package com.kii.beehive.business.event.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.EventProcess;
import com.kii.beehive.business.service.ThingGroupStateService;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Component
public class TagChangeProcess implements EventProcess {


	@Autowired
	private ThingGroupStateService triggerService;

	@Autowired
	private GlobalThingDao thingDao;

	@Override
	public void onEventFire(String triggerID, EventParam param) {

		List<String> relationThings= (List<String>) param.getParam("thingIDs");


		List<GlobalThingInfo>  things=thingDao.getThingsByIDArray(relationThings);


		triggerService.updateThingGroup(things,triggerID);


	}
}
