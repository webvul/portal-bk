package com.kii.beehive.business.event;

import java.util.Map;

import com.kii.beehive.portal.event.EventParam;

public interface BeehiveEventProcess {

	 void onEventFire(String eventKey, EventParam param,Map<String,Object> customer);

}
