package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingStateNotifyCallbackService;
import com.kii.beehive.business.service.TriggerFireCallbackService;
import com.kii.beehive.portal.web.entity.StateUpload;
import com.kii.beehive.portal.web.entity.TriggerCallbackParam;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@RestController
@RequestMapping(path = "/callback", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class ExtensionCallbackController {

	@Autowired
	private TriggerFireCallbackService callbackService;

	@Autowired
	private ThingStateNotifyCallbackService stateNotifyService;


	@RequestMapping(path="/simpleCallback",method = { RequestMethod.POST })
	public void onSimpleTriggerFire(@RequestHeader("x-kii-appid") String appID,
									@RequestHeader("Authorization") String token,
									@RequestBody TriggerCallbackParam param){

		callbackService.onSimpleArrive(param.getThingID(),param.getTriggerID());
	}

	@RequestMapping(path="/positionCallback",method = { RequestMethod.POST })
	public void onPositionTriggerFire(@RequestHeader("x-kii-appid") String appID,
									  @RequestHeader("Authorization") String token,
									  @RequestBody TriggerCallbackParam param){

		callbackService.onPositiveArrive(param.getThingID(),param.getTriggerID());


	}

	@RequestMapping(path="/negationCallback",method = { RequestMethod.POST })
	public void onNegationTriggerFire(@RequestHeader("x-kii-appid") String appID,
									  @RequestHeader("Authorization") String token,
									  @RequestBody TriggerCallbackParam param){

		callbackService.onNegativeArrive(param.getThingID(),param.getTriggerID());

	}

	@RequestMapping(path="/stateChangedCallback",method = { RequestMethod.POST })
	public void onStateChangeFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody StateUpload status){


		stateNotifyService.onThingStateChange(appID,status.getTarget(), status.getState());

	}


	@RequestMapping(path="/thingCreatedCallback",method = { RequestMethod.POST })
	public void onThingCreatedFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody StateUpload status){


		stateNotifyService.onThingStateChange(appID,status.getTarget(), status.getState());

	}




}
