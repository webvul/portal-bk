package com.kii.beehive.portal.helper;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.entitys.ThingStateUpload;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
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
	
	

	@Async
	public void onEventFire(GlobalThingInfo thing, ThingStatus status,Date timestamp) {
		
//		thingManager.getThingByFull
		
		
		BusinessDataObject data=new BusinessDataObject();
		data.setBusinessType(BusinessObjType.Thing);
		data.setBusinessObjID(String.valueOf(thing.getId()));
		data.setData(status.getFields());
		data.setCreated(timestamp);
		
		engine.updateBusinessData(data);
		
		
		pushStatusUpload(thing,status,timestamp);
	}

	private  void pushStatusUpload(GlobalThingInfo globalThingInfo, ThingStatus status, Date timestamp){
		
		ThingStateUpload thingStateUpload = new ThingStateUpload();
		thingStateUpload.setGlobalThingID(globalThingInfo.getId());
		thingStateUpload.setAppID(globalThingInfo.getKiiAppID());
		thingStateUpload.setThingID(globalThingInfo.getKiiThingID());
		thingStateUpload.setState(status);
		thingStateUpload.setTimestamp(timestamp);
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
