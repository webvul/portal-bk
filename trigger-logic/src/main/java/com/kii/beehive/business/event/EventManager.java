package com.kii.beehive.business.event;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.EventListenerDao;

@Component
public class EventManager {


	private EventListenerDao  eventListenerDao;

	public void addTagChangeListener(String tagName,String triggerID){


	}


	public void addTagChangeListener(List<String> tagNames,String triggerID){


	}

	public void addThingStatusListener(String thingID,String triggerID){


	}


	public void addThingStatusListener(List<String> thingIDs,String triggerID){


	}

	public void disableTrigger(String triggerID){


	}

	public void enableTrigger(String triggerID){


	}

}
