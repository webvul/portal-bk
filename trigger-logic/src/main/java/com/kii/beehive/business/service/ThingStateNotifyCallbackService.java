package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.KiiCloudEventBus;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class ThingStateNotifyCallbackService {


	@Autowired
	private KiiCloudEventBus eventBus;

	@Autowired
	private ThingService  service;

	public void onThingStateChange(String kiiThingID, ThingStatus  status){


		eventBus.onStatusUploadFire(kiiThingID,status);

	}
}
