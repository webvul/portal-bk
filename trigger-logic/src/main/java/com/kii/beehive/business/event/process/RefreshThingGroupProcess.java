package com.kii.beehive.business.event.process;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.manager.ThingGroupStateManager;

@Component(KiicloudEventListenerService.REFRESH_THING_GROUP)
@TagChanged
public class RefreshThingGroupProcess implements BeehiveEventProcess {


	@Autowired
	private ThingGroupStateManager triggerService;


	@Override
	public void onEventFire(String triggerID, EventParam param,Map<String,Object> customer) {

		triggerService.onTagChanged(triggerID);


	}
}
