package com.kii.beehive.business.event.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventProcess;
import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.manager.ThingStateSummaryManager;

@Component(BusinessEventListenerService.REFRESH_SUMMARY_GROUP)
//@TagChanged
public class ModifySummaryListenerProcess implements BusinessEventProcess {


	@Autowired
	private ThingStateSummaryManager summaryService;

	@Override
	public void onEventFire(EventListener listener, EventParam param) {

		String groupID= (String) listener.getCustoms().get(BusinessEventListenerService.GROUP_NAME);

		summaryService.onTagChanged(listener.getTargetKey(),groupID);
	}
}
