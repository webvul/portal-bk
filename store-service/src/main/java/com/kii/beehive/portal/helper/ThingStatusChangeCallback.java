package com.kii.beehive.portal.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.entitys.ThingStateUpload;
import com.kii.beehive.portal.entitys.ThingStatusMsg;
import com.kii.beehive.portal.jedis.dao.MessageQueueDao;
import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class ThingStatusChangeCallback {


	@Autowired
	private MessageQueueDao messageQueueDao;

	@Autowired
	private ObjectMapper objectMapper;
	@Value("${thing.state.queue:thing_state_queue}")
	private String thingStateQueue;
	@Autowired
	private ThingTagManager thingManager;

	private Logger log= LoggerFactory.getLogger(ThingStatusChangeCallback.class);

	@Autowired
	private BeehiveTriggerService engine;
	
	

	
	public void onEventFire(ThingStatusMsg msg) {
		
//		thingManager.getThingByFull
		
		
		BusinessDataObject data=new BusinessDataObject();
		data.setBusinessType(BusinessObjType.Thing);
		data.setBusinessObjID(String.valueOf(msg.getThingID()));
		data.setData(msg.getStatus());
		data.setCreated(msg.getTimestamp());
		
		engine.updateBusinessData(data);
		
		
		pushStatusUpload(msg);
	}

	private  void pushStatusUpload(ThingStatusMsg msg){
		
		ThingStateUpload thingStateUpload = new ThingStateUpload();
		thingStateUpload.setGlobalThingID(msg.getThingID());
		thingStateUpload.setAppID(msg.getAppID());
		thingStateUpload.setThingID(msg.getKiiThingID());
		ThingStatus status=new ThingStatus();
		status.setFields(msg.getStatus());
		thingStateUpload.setState(status);
		thingStateUpload.setTimestamp(msg.getTimestamp());
		String postEventJsonStr = "";
		try {
			postEventJsonStr = objectMapper.writeValueAsString(thingStateUpload);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		//push redis
		messageQueueDao.lpush(thingStateQueue, postEventJsonStr);
		
	}

}
