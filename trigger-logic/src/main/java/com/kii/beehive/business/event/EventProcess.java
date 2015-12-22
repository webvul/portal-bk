package com.kii.beehive.business.event;

import com.kii.beehive.portal.event.EventParam;

public interface EventProcess {

	public void onEventFire(String eventKey, EventParam param);

}
