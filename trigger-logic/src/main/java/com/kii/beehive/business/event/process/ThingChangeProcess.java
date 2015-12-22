package com.kii.beehive.business.event.process;

import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.EventProcess;
import com.kii.beehive.portal.event.EventParam;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ThingChangeProcess implements EventProcess {


	


	@Override
	public void onEventFire(String eventKey, EventParam param) {


		ThingStatus  status= (ThingStatus) param.getParam("status");



	}
}
