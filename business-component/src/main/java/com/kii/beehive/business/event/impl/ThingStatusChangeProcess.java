package com.kii.beehive.business.event.impl;

import java.util.Date;

import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

public interface ThingStatusChangeProcess {

	void onEventFire(EventListener listener, ThingStatus status,String thingID,Date timestamp);

}
