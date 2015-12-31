package com.kii.beehive.business.event.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.business.manager.ThingGroupStateManager;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

@Component(KiicloudEventListenerService.REFRESH_THING_GROUP)
@TagChanged
public class RefreshThingGroupProcess implements BeehiveEventProcess {


	@Autowired
	private ThingGroupStateManager triggerService;

	@Autowired
	private GlobalThingDao thingDao;

	@Override
	public void onEventFire(String triggerID, EventParam param) {

		List<String> relationThings= (List<String>) param.getParam("thingIDs");


		List<GlobalThingInfo>  things=thingDao.getThingsByVendorIDArray(relationThings);


		triggerService.updateThingGroup(things,triggerID);


	}
}
