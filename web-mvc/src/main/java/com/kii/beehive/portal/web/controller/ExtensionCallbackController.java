package com.kii.beehive.portal.web.controller;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.entitys.ThingStatusMsg;
import com.kii.beehive.portal.helper.ThingStatusChangeCallback;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.ExSpaceBookManager;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.entity.CreatedThing;
import com.kii.beehive.portal.web.entity.StateUpload;
import com.kii.beehive.portal.web.help.InternalEventListenerRegistry;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
@RestController(value = "extensionCallbackController")
@RequestMapping(value = CallbackNames.CALLBACK_URL, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ExtensionCallbackController {

	private static Logger log = LoggerFactory.getLogger(ExtensionCallbackController.class);


	@Autowired
	private ThingTagManager tagManager;


	@Autowired
	private ThingStatusChangeCallback pushCallback;

	@Autowired
	private InternalEventListenerRegistry internalEventListenerRegistry;

	@Autowired
	private ExSpaceBookManager spaceBookManager;

	private ObjectMapper objectMapper;

	@Value("${thing.state.queue:thing_state_queue}")
	private String thingStateQueue;


	@PostConstruct
	public void init() {

		objectMapper = new ObjectMapper();

		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


	}

	@RequestMapping(value = "/" + CallbackNames.STATE_CHANGED, method = {RequestMethod.POST})
	public void onStateChangeFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody String body) {
		long startTime = System.currentTimeMillis();
		StateUpload status = null;
		try {
			status = objectMapper.readValue(body, StateUpload.class);
		} catch (IOException e) {

			log.error(e.getMessage());
			return;
		}

		tagManager.updateState(status.getState(), status.getThingID(), appID);

		GlobalThingInfo globalThingInfo = tagManager.getThingByFullKiiThingID(appID, status.getThingID());
		this.timeDiff(startTime, "updateState done", globalThingInfo.getVendorThingID());

		spaceBookManager.onFaceThingStateChange(globalThingInfo, status.getState(), status.getTimestamp());
		this.timeDiff(startTime, "onFaceThingStateChange done", globalThingInfo.getVendorThingID());
		
		ThingStatusMsg msg = new ThingStatusMsg(globalThingInfo, body, status.getTimestamp());
		pushCallback.onEventFire(msg);
		this.timeDiff(startTime, "onEventFire done", globalThingInfo.getVendorThingID());
		
		pushCallback.pushStatusUpload(msg);
		
		internalEventListenerRegistry.onStateChange(appID, status);
		this.timeDiff(startTime, "onStateChange done", globalThingInfo.getVendorThingID());

	}


	@RequestMapping(value = "/" + CallbackNames.THING_CREATED, method = {RequestMethod.POST})
	public void onThingCreatedFire(@RequestHeader("x-kii-appid") String appID,
								   @RequestHeader("Authorization") String token,
								   @RequestBody String body) {
		CreatedThing thing = null;
		try {
			thing = objectMapper.readValue(body, CreatedThing.class);
		} catch (IOException e) {

			log.error(e.getMessage());
			return;
		}

		log.info("onBoarding = " + thing.getVendorThingID());
		tagManager.updateKiicloudRelation(thing.getVendorThingID(), appID + "-" + thing.getThingID());
	}


	@RequestMapping(value = "/" + CallbackNames.THING_CMD_RESPONSE, method = {RequestMethod.POST})
	public void onThingCmdResponse(@RequestHeader("x-kii-appid") String appID,
								   @RequestHeader("Authorization") String token,
								   @RequestBody String body) {
		ThingCommand cmd = null;
		try {
			cmd = objectMapper.readValue(body, ThingCommand.class);
		} catch (IOException e) {

			log.error(e.getMessage());
			return;
		}

		log.info("cmdResponse  " + cmd.getTarget());

//		commandService.saveComandResponse(cmd);
	}

	private void timeDiff(long startTime, String name, String vendorThingID) {
		if (vendorThingID.indexOf("0999") == 0) {
			log.info(name + " = " + vendorThingID + " = " + (Instant.now().toEpochMilli() - startTime));
		}
	}
}
