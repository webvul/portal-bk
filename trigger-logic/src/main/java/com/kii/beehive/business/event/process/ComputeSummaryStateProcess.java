package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.portal.event.annotation.ThingStateChange;
import com.kii.beehive.business.manager.ThingStateSummaryManager;
import com.kii.beehive.portal.event.EventParam;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(KiicloudEventListenerService.COMPUTE_SUMMARY_STATE)
@ThingStateChange
public class ComputeSummaryStateProcess implements BeehiveEventProcess {


	@Autowired
	private ThingStateSummaryManager summaryService;


	@Override
	public void onEventFire(String summaryID, EventParam param) {

		ThingStatus  status= (ThingStatus) param.getParam("status");

		summaryService.computeStateSummary(summaryID,status);

	}
}
