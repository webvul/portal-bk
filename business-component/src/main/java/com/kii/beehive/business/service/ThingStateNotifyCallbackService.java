package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ThingStateNotifyCallbackService {


	@Autowired
	private BusinessEventBus eventBus;


	public void onThingStateChange(String kiiAppID,String kiiThingID, ThingStatus  status){


		String fullThingID=ThingIDTools.joinFullKiiThingID(kiiThingID,kiiAppID);

		eventBus.onStatusUploadFire(fullThingID,status);

	}
}
