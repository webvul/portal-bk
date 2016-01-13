package com.kii.beehive.business.event.process;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.portal.manager.ThingStateSummaryManager;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.annotation.ThingStateChange;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(KiicloudEventListenerService.COMPUTE_SUMMARY_STATE)
@ThingStateChange
public class ComputeSummaryStateProcess implements BeehiveEventProcess {


	@Autowired
	private ThingStateSummaryManager summaryService;


	@Override
	public void onEventFire(String summaryID, EventParam param,Map<String,Object> customer) {

		ThingStatus  status= (ThingStatus) param.getParam("status");

		String groupID= (String) customer.get(KiicloudEventListenerService.GROUP_NAME);

		summaryService.computeStateSummary(summaryID,groupID,status);

	}
}
