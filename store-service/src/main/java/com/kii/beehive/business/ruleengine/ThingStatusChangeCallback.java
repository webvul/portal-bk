package com.kii.beehive.business.ruleengine;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.entitys.ThingStateUpload;
import com.kii.beehive.portal.jedis.dao.MessageQueueDao;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ThingStatusChangeCallback {

	@Autowired
	private EngineService engine;

	@Autowired
	private MessageQueueDao messageQueueDao;

	@Autowired
	private ObjectMapper objectMapper;
	@Value("${thing.state.queue:thing_state_queue}")
	private String thingStateQueue;

	@Async
	public void onEventFire( ThingStatus status, String thingID,Date timestamp) {

		engine.updateThingStatus(thingID,status.getFields(),timestamp);
	}

	@Async
	public void pushStatusUpload(String appID, String thingID, ThingStatus status, Date timestamp){
		ThingStateUpload thingStateUpload = new ThingStateUpload();
		thingStateUpload.setAppID(appID);
		thingStateUpload.setThingID(thingID);
		thingStateUpload.setState(status);
		thingStateUpload.setTimestamp(timestamp);
		String postEventJsonStr = "";
		try {
			postEventJsonStr = objectMapper.writeValueAsString(thingStateUpload);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		//push redis
		messageQueueDao.lpush(thingStateQueue, postEventJsonStr);
	}

}
