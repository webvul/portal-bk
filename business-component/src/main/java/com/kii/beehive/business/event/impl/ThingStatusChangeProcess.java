package com.kii.beehive.business.event.impl;

import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.ruleengine.sdk.entity.thingif.ThingStatus;

public interface ThingStatusChangeProcess {

	void onEventFire(EventListener listener, ThingStatus status,String thingID);

}
