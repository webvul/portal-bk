package com.kii.beehive.business.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.service.EventListenerDao;

@Component
public class KiicloudEventListenerService {


	public static final String COMPUTE_SUMMARY_STATE = "computeSummaryState";

	public static final String REFRESH_SUMMARY_GROUP="refreshSummaryGroup";

	public static final String REFRESH_THING_GROUP="refreshThingGroup";

	private EventListenerDao  eventListenerDao;

	public void addTagChangeListener(List<String> tagNames,String triggerID){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);

		listener.addBindKeys(tagNames);
		listener.setRelationBeanName(REFRESH_THING_GROUP);

		eventListenerDao.addEventListener(listener);

	}

	public String addSummaryChangeListener(List<String> tagNames,String summaryThingID){


		EventListener  listener=new EventListener();
		listener.setTargetKey(summaryThingID);

		listener.addBindKeys(tagNames);
		listener.setRelationBeanName(REFRESH_SUMMARY_GROUP);

		return eventListenerDao.addEventListener(listener);

	}


	public String addThingStatusListener(List<String> thingIDs,String summaryThingID){

		EventListener  listener=new EventListener();
		listener.setTargetKey(summaryThingID);
		listener.addBindKeys(thingIDs.stream().map(v->String.valueOf(v)).collect(Collectors.toList()));

		listener.setRelationBeanName(COMPUTE_SUMMARY_STATE);

		return eventListenerDao.addEventListener(listener);

	}

	public void updateThingStatusListener(List<String> thingIDs,String listenerID){

//		EventListener  listener=new EventListener();
//		listener.setTargetKey(summaryThingID);
//		listener.addBindKeys(thingIDs.stream().map(v->String.valueOf(v)).collect(Collectors.toList()));
//
//		listener.setRelationBeanName(COMPUTE_SUMMARY_STATE);

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

}
