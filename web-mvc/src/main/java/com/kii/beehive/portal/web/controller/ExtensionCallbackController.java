package com.kii.beehive.portal.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.ruleengine.ThingStatusChangeCallback;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.entity.CreatedThing;
import com.kii.beehive.portal.web.entity.StateUpload;
import com.kii.beehive.portal.web.help.InternalEventListenerRegistry;

@RestController(value = "extensionCallbackController")
@RequestMapping(value = CallbackNames.CALLBACK_URL, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ExtensionCallbackController {

	private static Logger log = LoggerFactory.getLogger(ExtensionCallbackController.class);

	@Autowired
	private ThingTagManager tagManager;

	@Autowired
	private ThingStatusChangeCallback statusChangeCallback;

	@Autowired
	private BusinessEventBus eventBus;

	@Autowired
	private InternalEventListenerRegistry internalEventListenerRegistry;


	@RequestMapping(value = "/" + CallbackNames.STATE_CHANGED, method = {RequestMethod.POST})
	public void onStateChangeFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody StateUpload status) {

		tagManager.updateState(status.getState(), status.getThingID(), appID);

		String fullThingID = ThingIDTools.joinFullKiiThingID(appID, status.getThingID());

		statusChangeCallback.onEventFire(status.getState(), fullThingID, status.getTimestamp());

		statusChangeCallback.pushStatusUpload(appID, status.getThingID(), status.getState(), status.getTimestamp());

		eventBus.onStatusUploadFire(fullThingID, status.getState(), status.getTimestamp());

		internalEventListenerRegistry.onStateChange(appID, status);
	}


	@RequestMapping(value = "/" + CallbackNames.THING_CREATED, method = {RequestMethod.POST})
	public void onThingCreatedFire(@RequestHeader("x-kii-appid") String appID,
								   @RequestHeader("Authorization") String token,
								   @RequestBody CreatedThing thing) {
		log.info("onBoarding = " + thing.getVendorThingID());
		tagManager.updateKiicloudRelation(thing.getVendorThingID(), appID + "-" + thing.getThingID());
	}


}
