package com.kii.beehive.business.event;

import java.util.Collection;
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
public class BusinessEventListenerService {


	public static final String COMPUTE_SUMMARY_STATE = "computeSummaryState";

	public static final String REFRESH_SUMMARY_GROUP="refreshSummaryGroup";

	public static final String REFRESH_THING_GROUP="refreshThingGroup";

	public static final String REFRESH_THING_FOR_TRIGGER="refreshThingForTrigger";

	public static final String FIRE_TRIGGER_WHEN_MATCH="fireTriggerWhenMatch";

	public static final String GROUP_NAME = "groupName";

//	public static final String TRIGGER_TYPE = "triggerType";

	@Autowired
	private EventListenerDao  eventListenerDao;

	public String addBeehiveTriggerChangeListener(String targetKey,String businessTriggerID){

		EventListener  listener=new EventListener();
		listener.setTargetKey(targetKey);

		listener.addBindKey(businessTriggerID);

		listener.setRelationBeanName(FIRE_TRIGGER_WHEN_MATCH);
		listener.setEnable(true);
		listener.setType(EventType.TriggerFire);
//		listener.addCustomValue(TRIGGER_TYPE,type);

		return eventListenerDao.addEventListener(listener);


	}

	public String addGroupTagChangeListener(Collection<String> tagNames, String triggerID){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);

		listener.addBindKeys(tagNames);
		listener.setRelationBeanName(REFRESH_THING_GROUP);
		listener.setEnable(true);
		listener.setType(EventType.TagChange);

		return eventListenerDao.addEventListener(listener);

	}

	public String addSummaryTagChangeListener(Collection<String> tagNames, String triggerID, String name){


		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addCustomValue(GROUP_NAME,name);

		listener.addBindKeys(tagNames);
		listener.setEnable(true);
		listener.setRelationBeanName(REFRESH_SUMMARY_GROUP);
		listener.setType(EventType.TagChange);

		return eventListenerDao.addEventListener(listener);

	}

	public String addThingStatusListenerForSummary(Collection<String> thingIDs, String triggerID, String name){

		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addCustomValue("groupName",name);
		listener.addBindKeys(thingIDs.stream().map(v->String.valueOf(v)).collect(Collectors.toList()));

		listener.setRelationBeanName(COMPUTE_SUMMARY_STATE);
		listener.setType(EventType.ThingStateChange);
		listener.setEnable(true);

		return eventListenerDao.addEventListener(listener);

	}

	public String addThingStatusListenerForTrigger(Collection<String> thingIDs, String triggerID){

		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
		listener.addBindKeys(thingIDs.stream().map(v->String.valueOf(v)).collect(Collectors.toList()));

		listener.setRelationBeanName(REFRESH_THING_FOR_TRIGGER);
		listener.setType(EventType.ThingStateChange);
		listener.setEnable(true);

		return eventListenerDao.addEventListener(listener);

	}
//
	public void updateThingStatusListener(Collection<String> thingIDs,String listenerID){


		Map<String,Boolean>  thingMap=new HashMap<>();
		thingIDs.forEach(id->{
			thingMap.put(id,true);
		});
		Map<String,Object> param=Collections.singletonMap("bindKeys",thingMap);

		 eventListenerDao.updateEntity(param,listenerID);

	}



	public void disableTriggerByTargetID(String triggerID){

		List<EventListener> listener=eventListenerDao.getEventListenerByTargetKey(triggerID);

		listener.stream().map(EventListener::getId).forEach(eventListenerDao::disableListener);

	}

	public void enableTriggerByTargetID(String triggerID){

		List<EventListener> listener=eventListenerDao.getEventListenerByTargetKey(triggerID);

		listener.stream().map(EventListener::getId).forEach(eventListenerDao::enableListener);

	}

	public void disableTrigger(String listenerID){

		eventListenerDao.disableListener(listenerID);

	}

//	public void enableTrigger(String listenerID){
//
//		eventListenerDao.enableListener(listenerID);
//
//	}
	
	
	public void removeListener(String listenerID) {

		eventListenerDao.removeEntity(listenerID);
	}
}
