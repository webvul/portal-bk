package com.kii.beehive.business.event.process;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.portal.event.EventParam;
import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.manager.ThingStateSummaryManager;

@Component(KiicloudEventListenerService.REFRESH_SUMMARY_GROUP)
@TagChanged
public class ModifySummaryListenerProcess implements BeehiveEventProcess {


	@Autowired
	private ThingStateSummaryManager summaryService;

	@Override
	public void onEventFire(String eventKey, EventParam param,Map<String,Object> customer) {

		String groupID= (String) customer.get("groupID");

		summaryService.onTagChanged(eventKey,groupID);
	}
}
