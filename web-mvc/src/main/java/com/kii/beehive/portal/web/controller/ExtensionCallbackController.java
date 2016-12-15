package com.kii.beehive.portal.web.controller;

import javax.annotation.PostConstruct;

import java.util.Map;

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

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.ruleengine.ThingCommandForTriggerService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.helper.ThingStatusChangeCallback;
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
	private BusinessEventBus eventBus;

	@Autowired
	private InternalEventListenerRegistry internalEventListenerRegistry;

	@Autowired
	private ThingCommandForTriggerService  commandService;

	private ObjectMapper objectMapper;
	@Value("${thing.state.queue:thing_state_queue}")
	private String thingStateQueue;


	
	@PostConstruct
	public void init(){
		
		objectMapper=new ObjectMapper();
		
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY,true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		
		
	}

	@RequestMapping(value = "/" + CallbackNames.STATE_CHANGED, method = {RequestMethod.POST})
	public void onStateChangeFire(@RequestHeader("x-kii-appid") String appID,
								  @RequestHeader("Authorization") String token,
								  @RequestBody StateUpload status) {

		Map<String,Object> values=tagManager.updateState(status.getState(), status.getThingID(), appID);

		String fullThingID = ThingIDTools.joinFullKiiThingID(appID, status.getThingID());

		pushCallback.onEventFire(appID,status.getState(),fullThingID,status.getTimestamp());

		eventBus.onStatusUploadFire(fullThingID, status.getState(), status.getTimestamp());

		status.getState().setFields(values);

		internalEventListenerRegistry.onStateChange(appID, status);
	}


	@RequestMapping(value = "/" + CallbackNames.THING_CREATED, method = {RequestMethod.POST})
	public void onThingCreatedFire(@RequestHeader("x-kii-appid") String appID,
								   @RequestHeader("Authorization") String token,
								   @RequestBody CreatedThing thing) {
		log.info("onBoarding = " + thing.getVendorThingID());
		tagManager.updateKiicloudRelation(thing.getVendorThingID(), appID + "-" + thing.getThingID());
	}


	@RequestMapping(value = "/" + CallbackNames.THING_CMD_RESPONSE, method = {RequestMethod.POST})
	public void onThingCmdResponse(@RequestHeader("x-kii-appid") String appID,
								   @RequestHeader("Authorization") String token,
								   @RequestBody ThingCommand cmd) {


		log.info("cmdResponse  " + cmd.getTarget());


		commandService.saveComandResponse(cmd);


	}

}
