package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingStateNotifyCallbackService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.entity.CreatedThing;
import com.kii.beehive.portal.web.entity.StateUpload;

@RestController
@RequestMapping(path = CallbackNames.CALLBACK_URL, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class ExtensionCallbackController {


	@Autowired
	private ThingStateNotifyCallbackService stateNotifyService;

	@Autowired
	private ThingTagManager tagManager;



	@RequestMapping(path= "/" + CallbackNames.STATE_CHANGED,method = { RequestMethod.POST })
	public void onStateChangeFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody StateUpload status){

		tagManager.updateState(status.getState(),status.getTarget(),appID);


		stateNotifyService.onThingStateChange(appID,status.getTarget(), status.getState());

	}


	@RequestMapping(path= "/" + CallbackNames.THING_CREATED,method = { RequestMethod.POST })
	public void onThingCreatedFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody CreatedThing thing){



		tagManager.updateKiicloudRelation(thing.getVendorThingID(),appID+"-"+thing.getThingID());
	}




}
