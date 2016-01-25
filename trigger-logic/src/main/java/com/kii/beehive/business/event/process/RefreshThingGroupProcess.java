package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventProcess;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.manager.ThingGroupStateManager;

@Component(BusinessEventListenerService.REFRESH_THING_GROUP)
@TagChanged
public class RefreshThingGroupProcess implements BusinessEventProcess {


	@Autowired
	private ThingGroupStateManager triggerService;


	@Override
	public void onEventFire(EventListener listener, EventParam param) {

		triggerService.onTagChanged(listener.getTargetKey());


	}
}
