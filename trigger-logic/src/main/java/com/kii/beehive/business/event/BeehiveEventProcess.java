package com.kii.beehive.business.event;

import com.kii.beehive.portal.event.EventParam;

public interface BeehiveEventProcess {

	 void onEventFire(String eventKey, EventParam param);

}
