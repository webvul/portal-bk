package com.kii.beehive.business.event;

import java.util.Map;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventParam;

public interface BusinessEventProcess {

	 void onEventFire(EventListener  listener, EventParam param);

}
