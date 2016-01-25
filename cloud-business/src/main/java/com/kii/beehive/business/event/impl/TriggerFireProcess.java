package com.kii.beehive.business.event.impl;

import com.kii.beehive.portal.event.EventListener;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;

public interface TriggerFireProcess {

	void onEventFire(EventListener listener,String thingID,TriggerWhen when,boolean sign);

}
