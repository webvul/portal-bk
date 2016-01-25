package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventProcess;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.manager.ThingStateSummaryManager;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component(BusinessEventListenerService.COMPUTE_SUMMARY_STATE)
//@ThingStateChange
public class ComputeSummaryStateProcess implements BusinessEventProcess {


	@Autowired
	private ThingStateSummaryManager summaryService;


	@Override
	public void onEventFire(EventListener listener, EventParam param) {

		ThingStatus  status= (ThingStatus) param.getParam("status");

		String groupID= (String) listener.getCustoms().get(BusinessEventListenerService.GROUP_NAME);

		summaryService.computeStateSummary(listener.getTargetKey(),groupID,status);

	}
}
