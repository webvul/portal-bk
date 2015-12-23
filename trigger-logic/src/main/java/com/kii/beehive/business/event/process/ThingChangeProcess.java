package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.service.ThingStateSummaryService;
import com.kii.beehive.portal.event.EventParam;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(KiicloudEventListenerService.THING_STATE_CHANGE)
public class ThingChangeProcess implements BeehiveEventProcess {


	@Autowired
	private ThingStateSummaryService  summaryService;


	@Override
	public void onEventFire(String eventKey, EventParam param) {

		ThingStatus  status= (ThingStatus) param.getParam("status");

		summaryService.computeStateSummary(eventKey,status);

	}
}
