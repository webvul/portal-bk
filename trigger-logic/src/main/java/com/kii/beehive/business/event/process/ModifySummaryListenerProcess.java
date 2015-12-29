package com.kii.beehive.business.event.process;

import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BeehiveEventProcess;
import com.kii.beehive.business.event.KiicloudEventListenerService;
import com.kii.beehive.portal.event.annotation.TagChanged;
import com.kii.beehive.portal.event.EventParam;

@Component(KiicloudEventListenerService.REFRESH_SUMMARY_GROUP)
@TagChanged
public class ModifySummaryListenerProcess implements BeehiveEventProcess {


	@Override
	public void onEventFire(String eventKey, EventParam param) {

	}
}
