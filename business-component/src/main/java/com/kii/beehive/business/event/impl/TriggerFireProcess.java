package com.kii.beehive.business.event.impl;

import com.kii.beehive.portal.event.EventListener;

public interface TriggerFireProcess {

	void onEventFire(EventListener listener,String thingID);

}
