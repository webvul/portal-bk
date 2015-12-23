package com.kii.beehive.business.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.service.EventListenerDao;

@Component
public class KiicloudEventListenerService {


	public static final String TAG_CHANGE = "tagChange";

	public static final String THING_STATE_CHANGE="thingStateChange";

	private EventListenerDao  eventListenerDao;

	public void addTagChangeListener(String tagName,String triggerID){

		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addBindKey(tagName);
		listener.setRelationBeanName(TAG_CHANGE);

		eventListenerDao.addEventListener(listener);
	}


	public void addTagChangeListener(List<String> tagNames,String triggerID){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);

		listener.addBindKeys(tagNames);
		listener.setRelationBeanName(TAG_CHANGE);

		eventListenerDao.addEventListener(listener);

	}

	public void addThingStatusListener(String thingID,String triggerID){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addBindKey(triggerID);
		listener.setRelationBeanName(TAG_CHANGE);

		eventListenerDao.addEventListener(listener);

	}


	public void addThingStatusListener(List<String> thingIDs,String triggerID){

		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addBindKeys(thingIDs);
		listener.setRelationBeanName(TAG_CHANGE);

		eventListenerDao.addEventListener(listener);

	}

	public void disableTrigger(String triggerID){

		List<EventListener> listener=eventListenerDao.getEventListenerByTargetKey(triggerID);

		listener.stream().map(EventListener::getId).forEach(eventListenerDao::disableListener);

	}

	public void enableTrigger(String triggerID){

		List<EventListener> listener=eventListenerDao.getEventListenerByTargetKey(triggerID);

		listener.stream().map(EventListener::getId).forEach(eventListenerDao::enableListener);

	}

}
