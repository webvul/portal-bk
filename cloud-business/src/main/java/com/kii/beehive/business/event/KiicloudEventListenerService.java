package com.kii.beehive.business.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.event.EventType;
import com.kii.beehive.portal.service.EventListenerDao;

@Component
public class KiicloudEventListenerService {


	public static final String COMPUTE_SUMMARY_STATE = "computeSummaryState";

	public static final String REFRESH_SUMMARY_GROUP="refreshSummaryGroup";

	public static final String REFRESH_THING_GROUP="refreshThingGroup";

	@Autowired
	private EventListenerDao  eventListenerDao;

	public String addTagChangeListener(List<String> tagNames,String triggerID){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);

		listener.addBindKeys(tagNames);
		listener.setRelationBeanName(REFRESH_THING_GROUP);
		listener.setEnable(true);
		listener.setType(EventType.TagChange);

		return eventListenerDao.addEventListener(listener);

	}



//
	public String addSummaryChangeListener(List<String> tagNames,String triggerID,String name){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addCustomValue("groupName",name);

		listener.addBindKeys(tagNames);
		listener.setEnable(true);
		listener.setRelationBeanName(REFRESH_SUMMARY_GROUP);
		listener.setType(EventType.TagChange);

		return eventListenerDao.addEventListener(listener);

	}
//
//
	public String addThingStatusListener(List<String> thingIDs,String triggerID,String name){

		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addCustomValue("groupName",name);
		listener.addBindKeys(thingIDs.stream().map(v->String.valueOf(v)).collect(Collectors.toList()));

		listener.setRelationBeanName(COMPUTE_SUMMARY_STATE);
		listener.setType(EventType.ThingStateChange);
		listener.setEnable(true);

		return eventListenerDao.addEventListener(listener);

	}
//
	public void updateThingStatusListener(List<String> thingIDs,String listenerID){


		Map<String,Boolean>  thingMap=new HashMap<>();
		thingIDs.forEach(id->{
			thingMap.put(id,true);
		});
		Map<String,Object> param=Collections.singletonMap("bindKeys",thingMap);

		 eventListenerDao.updateEntity(param,listenerID);

	}



	public void disableTrigger(String triggerID){

		List<EventListener> listener=eventListenerDao.getEventListenerByTargetKey(triggerID);

		listener.stream().map(EventListener::getId).forEach(eventListenerDao::disableListener);

	}

	public void enableTrigger(String triggerID){

		List<EventListener> listener=eventListenerDao.getEventListenerByTargetKey(triggerID);

		listener.stream().map(EventListener::getId).forEach(eventListenerDao::enableListener);

	}
	
	
	public void removeListener(String listenerID) {

		eventListenerDao.removeEntity(listenerID);
	}
}
