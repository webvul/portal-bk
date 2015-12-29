package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.KiiCloudEventBus;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ThingStateNotifyCallbackService {


	@Autowired
	private KiiCloudEventBus eventBus;

	@Autowired
	private ThingTagService service;

	public void onThingStateChange(String kiiAppID,String kiiThingID, ThingStatus  status){

		GlobalThingInfo  thing=service.getThingByKiiThingID(kiiAppID,kiiThingID);

		eventBus.onStatusUploadFire(thing.getVendorThingID(),status);

	}
}
